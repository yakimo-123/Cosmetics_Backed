package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.CategoryRequestDto;
import org.cosmetic.com.dto.response.CategoryResponseDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAll() {
        List<CategoryResponseDto> categories = categoryService.findAll();
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponseDto>>builder()
                        .status(true)
                        .message("Fetched all categories")
                        .data(categories)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getById(@PathVariable Long id) {
        CategoryResponseDto dto = categoryService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponseDto>builder()
                        .status(true)
                        .message("Category found")
                        .data(dto)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(@RequestBody CategoryRequestDto dto) {
        CategoryResponseDto created = categoryService.save(dto);
        return ResponseEntity.status(201).body(
                ApiResponse.<CategoryResponseDto>builder()
                        .status(true)
                        .message("Category created")
                        .data(created)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> update(@PathVariable Long id, @RequestBody CategoryRequestDto dto) {
        CategoryResponseDto updated = categoryService.update(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponseDto>builder()
                        .status(true)
                        .message("Category updated")
                        .data(updated)
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