package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.CategoryRequestDto;
import org.cosmetic.com.dto.response.CategoryResponseDto;
import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> findAll();
    CategoryResponseDto findById(Long id);
    CategoryResponseDto save(CategoryRequestDto requestDto);
    CategoryResponseDto update(Long id, CategoryRequestDto dto);
    void deleteById(Long id);
}