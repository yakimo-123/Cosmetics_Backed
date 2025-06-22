package org.cosmetic.com.service.impl;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.BrandMapper;
import org.cosmetic.com.model.Brand;
import org.cosmetic.com.repository.BrandRepository;
import org.cosmetic.com.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public Brand save(BrandRequestDto dto) {
        Brand brand = brandMapper.toEntity(dto);
        return brandRepository.save(brand);
    }

    @Override
    public Brand update(Long id, BrandRequestDto dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        brand.setName(dto.getName());
        return brandRepository.save(brand);
    }

    @Override
    public void deleteById(Long id) {
        brandRepository.deleteById(id);
    }
}