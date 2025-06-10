package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.CategoryRequestDto;
import org.cosmetic.com.dto.response.CategoryResponseDto;
import org.cosmetic.com.dto.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAll() {
        List<Category> categories = categoryService.findAll();
        List<CategoryResponseDto> response = categories.stream()
                .map(categoryMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
            ApiResponse.<List<CategoryResponseDto>>builder()
                .status(true)
                .message("Fetched all categories")
                .data(response)
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(categoryMapper::toResponseDto)
                .map(dto -> ResponseEntity.ok(
                    ApiResponse.<CategoryResponseDto>builder()
                        .status(true)
                        .message("Category found")
                        .data(dto)
                        .build()
                ))
                .orElse(ResponseEntity.status(404).body(
                    ApiResponse.<CategoryResponseDto>builder()
                        .status(false)
                        .message("Category not found")
                        .data(null)
                        .build()
                ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(@RequestBody CategoryRequestDto dto) {
        Category category = categoryService.save(dto);
        return ResponseEntity.status(201).body(
            ApiResponse.<CategoryResponseDto>builder()
                .status(true)
                .message("Category created")
                .data(categoryMapper.toResponseDto(category))
                .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> update(@PathVariable Long id, @RequestBody CategoryRequestDto dto) {
        Category updated = categoryService.update(id, dto);
        return ResponseEntity.ok(
            ApiResponse.<CategoryResponseDto>builder()
                .status(true)
                .message("Category updated")
                .data(categoryMapper.toResponseDto(updated))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .status(true)
                .message("Category deleted")
                .data(null)
                .build()
        );
    }
}