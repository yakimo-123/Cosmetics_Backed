package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.BrandResponseDto;
import org.cosmetic.com.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponseDto>>> getAll() {
        List<BrandResponseDto> brands = brandService.findAll();
        return ResponseEntity.ok(
                ApiResponse.<List<BrandResponseDto>>builder()
                        .status(true)
                        .message("Fetched all brands")
                        .data(brands)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDto>> getById(@PathVariable Long id) {
        BrandResponseDto dto = brandService.findById(id); // đã throw nếu not found
        return ResponseEntity.ok(
                ApiResponse.<BrandResponseDto>builder()
                        .status(true)
                        .message("Brand found")
                        .data(dto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponseDto>> create(@RequestBody BrandRequestDto dto) {
        BrandResponseDto created = brandService.save(dto);
        return ResponseEntity.status(201).body(
                ApiResponse.<BrandResponseDto>builder()
                        .status(true)
                        .message("Brand created")
                        .data(created)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDto>> update(@PathVariable Long id, @RequestBody BrandRequestDto dto) {
        BrandResponseDto updated = brandService.update(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<BrandResponseDto>builder()
                        .status(true)
                        .message("Brand updated")
                        .data(updated)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        brandService.deleteById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(true)
                        .message("Brand deleted")
                        .data(null)
                        .build()
        );
    }
}
