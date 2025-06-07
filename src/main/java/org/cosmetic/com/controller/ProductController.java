package org.cosmetic.com.controller;

import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.model.Product;
import org.cosmetic.com.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAll(pageable);
        ApiResponse<Page<Product>> response = ApiResponse.<Page<Product>>builder()
                .status(true)
                .message("Products retrieved successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(ApiResponse.<Product>builder()
                        .status(true)
                        .message("Product retrieved successfully")
                        .data(product)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @RequestBody ProductRequestDto productRequestDto,
            @RequestParam("images") List<MultipartFile> images
    ) {
        Product savedProduct = productService.save(productRequestDto);
        ApiResponse<Product> response = ApiResponse.<Product>builder()
                .status(true)
                .message("Product created successfully")
                .data(savedProduct)
                .build();
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.findById(id)
                .map(existingProduct -> {
                    product.setId(id);
                    Product updatedProduct = productService.save(product);
                    ApiResponse<Product> response = ApiResponse.<Product>builder()
                            .status(true)
                            .message("Product updated successfully")
                            .data(updatedProduct)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
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