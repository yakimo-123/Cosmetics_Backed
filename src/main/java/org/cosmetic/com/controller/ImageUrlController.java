package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.enums.ImageType;
import org.cosmetic.com.model.ImageUrl;
import org.cosmetic.com.service.ImageUrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageUrlController {

    private final ImageUrlService imageUrlService;


    @GetMapping("/type/{imageType}/{id}")
    public ResponseEntity<ApiResponse<List<ImageUrl>>> getImagesByTypeAndId(
            @PathVariable ImageType imageType,
            @PathVariable long id) {
        List<ImageUrl> images = imageUrlService.findByImageTypeAndId(imageType, id);
        ApiResponse<List<ImageUrl>> response = ApiResponse.<List<ImageUrl>>builder()
                .status(true)
                .message("Retrieved images successfully")
                .data(images)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ImageUrl>> getImageById(@PathVariable Long id) {
        ImageUrl image = imageUrlService.findById(id);
        ApiResponse<ImageUrl> response = ApiResponse.<ImageUrl>builder()
                .status(true)
                .message("Retrieved image successfully")
                .data(image)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ImageUrl>> createImage(@RequestBody ImageUrl imageUrl) {
        ImageUrl savedImage = imageUrlService.save(imageUrl);
        ApiResponse<ImageUrl> response = ApiResponse.<ImageUrl>builder()
                .status(true)
                .message("Image created successfully")
                .data(savedImage)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long id) {
        imageUrlService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(true)
                .message("Image deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}