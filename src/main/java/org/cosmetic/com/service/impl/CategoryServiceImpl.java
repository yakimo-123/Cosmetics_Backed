package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.CategoryRequestDto;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.CategoryMapper;
import org.cosmetic.com.model.Category;
import org.cosmetic.com.repository.CategoryRepository;
import org.cosmetic.com.service.CategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable(value = "categories")
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Cacheable(value = "category", key = "#id")
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public Category save(CategoryRequestDto requestDto) {
        Category category = categoryMapper.toEntity(requestDto);
        return categoryRepository.save(category);
    }

    @Override
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @CacheEvict(value = {"categories"}, allEntries = true)
    @CachePut(value = "category", key = "#id")
    public Category update(Long id, CategoryRequestDto dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        existing.setCategoryName(dto.getCategoryName());
        return categoryRepository.save(existing);
    }
}
