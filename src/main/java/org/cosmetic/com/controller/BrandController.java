package org.cosmetic.com.controller;

    import lombok.RequiredArgsConstructor;
    import org.cosmetic.com.dto.request.BrandRequestDto;
    import org.cosmetic.com.dto.response.BrandResponseDto;
    import org.cosmetic.com.dto.response.ApiResponse;
    import org.cosmetic.com.mapper.BrandMapper;
    import org.cosmetic.com.model.Brand;
    import org.cosmetic.com.service.BrandService;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import java.util.List;

    @RestController
    @RequestMapping("/api/brands")
    @RequiredArgsConstructor
    public class BrandController {

        private final BrandService brandService;
        private final BrandMapper brandMapper;

        @GetMapping
        public ResponseEntity<ApiResponse<List<BrandResponseDto>>> getAll() {
            List<Brand> brands = brandService.findAll();
            List<BrandResponseDto> response = brands.stream()
                    .map(brandMapper::toResponseDto)
                    .toList();
            return ResponseEntity.ok(
                ApiResponse.<List<BrandResponseDto>>builder()
                    .status(true)
                    .message("Fetched all brands")
                    .data(response)
                    .build()
            );
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<BrandResponseDto>> getById(@PathVariable Long id) {
            return brandService.findById(id)
                    .map(brandMapper::toResponseDto)
                    .map(dto -> ResponseEntity.ok(
                        ApiResponse.<BrandResponseDto>builder()
                            .status(true)
                            .message("Brand found")
                            .data(dto)
                            .build()
                    ))
                    .orElse(ResponseEntity.status(404).body(
                        ApiResponse.<BrandResponseDto>builder()
                            .status(false)
                            .message("Brand not found")
                            .data(null)
                            .build()
                    ));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<BrandResponseDto>> create(@RequestBody BrandRequestDto dto) {
            Brand brand = brandService.save(dto);
            return ResponseEntity.status(201).body(
                ApiResponse.<BrandResponseDto>builder()
                    .status(true)
                    .message("Brand created")
                    .data(brandMapper.toResponseDto(brand))
                    .build()
            );
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<BrandResponseDto>> update(@PathVariable Long id, @RequestBody BrandRequestDto dto) {
            Brand updated = brandService.update(id, dto);
            return ResponseEntity.ok(
                ApiResponse.<BrandResponseDto>builder()
                    .status(true)
                    .message("Brand updated")
                    .data(brandMapper.toResponseDto(updated))
                    .build()
            );
        }

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