package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.dto.response.BrandResponseDto;
import org.cosmetic.com.mapper.BrandMapper;
import org.cosmetic.com.model.Brand;
import org.cosmetic.com.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final BrandMapper brandMapper;

    @GetMapping
    public ResponseEntity<List<BrandResponseDto>> getAll() {
        List<Brand> brands = brandService.findAll();
        List<BrandResponseDto> response = brands.stream()
                .map(brandMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponseDto> getById(@PathVariable Long id) {
        return brandService.findById(id)
                .map(brandMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BrandResponseDto> create(@RequestBody BrandRequestDto dto) {
        Brand brand = brandService.save(dto);
        return ResponseEntity.ok(brandMapper.toResponseDto(brand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponseDto> update(@PathVariable Long id, @RequestBody BrandRequestDto dto) {
        Brand updated = brandService.update(id, dto);
        return ResponseEntity.ok(brandMapper.toResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}