package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.CategoryRequestDto;
import org.cosmetic.com.dto.response.CategoryResponseDto;
import org.cosmetic.com.mapper.CategoryMapper;
import org.cosmetic.com.model.Category;
import org.cosmetic.com.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        List<Category> categories = categoryService.findAll();
        List<CategoryResponseDto> response = categories.stream()
                .map(categoryMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(categoryMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@RequestBody CategoryRequestDto dto) {
        Category category = categoryService.save(dto);
        return ResponseEntity.ok(categoryMapper.toResponseDto(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable Long id, @RequestBody CategoryRequestDto dto) {
        Category updated = categoryService.update(id, dto);
        return ResponseEntity.ok(categoryMapper.toResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}