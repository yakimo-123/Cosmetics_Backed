package org.cosmetic.com.service.impl;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.dto.response.BrandResponseDto;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.BrandMapper;
import org.cosmetic.com.model.Brand;
import org.cosmetic.com.repository.BrandRepository;
import org.cosmetic.com.service.BrandService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Cacheable(value = "brands")
    public List<BrandResponseDto> findAll() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponseDto)
                .toList();
    }

    @Override
    @Cacheable(value = "brand", key = "#id")
    public BrandResponseDto findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        return brandMapper.toResponseDto(brand);
    }

    @Override
    @CacheEvict(value = {"brands"}, allEntries = true)
    public BrandResponseDto save(BrandRequestDto dto) {
        Brand brand = brandMapper.toEntity(dto);
        return brandMapper.toResponseDto(brandRepository.save(brand));
    }

    @Override
    @CachePut(value = "brand", key = "#id")
    @CacheEvict(value = {"brands"}, allEntries = true)
    public BrandResponseDto update(Long id, BrandRequestDto dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        brand.setName(dto.getName());
        return brandMapper.toResponseDto(brandRepository.save(brand));
    }

    @Override
    @CacheEvict(value = {"brands", "brand"}, allEntries = true)
    public void deleteById(Long id) {
        brandRepository.deleteById(id);
    }
}
