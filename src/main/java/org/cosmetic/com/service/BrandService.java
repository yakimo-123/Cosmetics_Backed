package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.model.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    List<Brand> findAll();
    Optional<Brand> findById(Long id);
    Brand save(BrandRequestDto dto);
    Brand update(Long id, BrandRequestDto dto);
    void deleteById(Long id);
}