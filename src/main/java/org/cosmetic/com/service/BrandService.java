package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.dto.response.BrandResponseDto;
import java.util.List;

public interface BrandService {
    List<BrandResponseDto> findAll();
    BrandResponseDto findById(Long id);
    BrandResponseDto save(BrandRequestDto dto);
    BrandResponseDto update(Long id, BrandRequestDto dto);
    void deleteById(Long id);
}