package org.cosmetic.com.service.impl;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.BrandMapper;
import org.cosmetic.com.model.Brand;
import org.cosmetic.com.repository.BrandRepository;
import org.cosmetic.com.service.BrandService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Cacheable(value = "brands")
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    @Cacheable(value = "brand", key = "#id") // cache theo id
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    @CacheEvict(value = {"brands"}, allEntries = true) // xóa danh sách khi thêm mới
    public Brand save(BrandRequestDto dto) {
        Brand brand = brandMapper.toEntity(dto);
        return brandRepository.save(brand);
    }

    @Override
    @CachePut(value = "brand", key = "#id") // cập nhật cache brand theo id
    @CacheEvict(value = {"brands"}, allEntries = true)
    public Brand update(Long id, BrandRequestDto dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        brand.setName(dto.getName());
        return brandRepository.save(brand);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "brands", allEntries = true),
            @CacheEvict(value = "brand", key = "#id")
    })
    public void deleteById(Long id) {
        brandRepository.deleteById(id);
    }
}