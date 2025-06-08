package org.cosmetic.com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.ProductResponseDto;
import org.cosmetic.com.mapper.ProductMapper;
import org.cosmetic.com.model.Product;
import org.cosmetic.com.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductMapper productMapper;
    private final ProductService productService;

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ProductResponseDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAll(pageable);
        Page<ProductResponseDto> productDtos = products.map(productMapper::toResponseDto);
        ApiResponse<Page<ProductResponseDto>> response = ApiResponse.<Page<ProductResponseDto>>builder()
                .status(true)
                .message("Products retrieved successfully")
                .data(productDtos)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {
        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isPresent()) {
            ProductResponseDto dto = productMapper.toResponseDto(productOpt.get());
            ApiResponse<ProductResponseDto> response = ApiResponse.<ProductResponseDto>builder()
                    .status(true)
                    .message("Product retrieved successfully")
                    .data(dto)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(
            @RequestPart("product") @Valid ProductRequestDto productRequestDto,
            @RequestPart("images") List<MultipartFile> images
    ) throws IOException {
        Product savedProduct = productService.save(productRequestDto, images);
        ApiResponse<ProductResponseDto> response = ApiResponse.<ProductResponseDto>builder()
                .status(true)
                .message("Product created successfully")
                .data(productMapper.toResponseDto(savedProduct))
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @Valid ProductRequestDto productRequestDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        Product updatedProduct = productService.update(id, productRequestDto, images);
        ApiResponse<ProductResponseDto> response = ApiResponse.<ProductResponseDto>builder()
                .status(true)
                .message("Product updated successfully")
                .data(productMapper.toResponseDto(updatedProduct))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        if (productService.findById(id).isPresent()) {
            productService.deleteById(id);
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .status(true)
                    .message("Product deleted successfully")
                    .data("Product with ID " + id + " deleted")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}