package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.ReviewRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.enums.ReviewStatus;
import org.cosmetic.com.model.Review;
import org.cosmetic.com.security.CustomUserDetails;
import org.cosmetic.com.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createReview(@RequestBody ReviewRequestDto requestDto) {
        Review created = reviewService.createReview(requestDto);
        return ResponseEntity.ok(ApiResponse.<Review>builder()
                .status(true)
                .message("Review created")
                .data(created)
                .build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<Review>>> getApprovedReviewsByProduct(@PathVariable String productId) {
        List<Review> reviews = reviewService.getApprovedReviewsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.<List<Review>>builder()
                .status(true)
                .message("Approved reviews retrieved")
                .data(reviews)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{reviewId}/status")
    public ResponseEntity<ApiResponse<Review>> updateReviewStatus(
            @PathVariable String reviewId,
            @RequestParam ReviewStatus status) {
        Review updated = reviewService.updateReviewStatus(reviewId, status);
        return ResponseEntity.ok(ApiResponse.<Review>builder()
                .status(true)
                .message("Review status updated")
                .data(updated)
                .build());
    }

    @GetMapping("/product/{productId}/all")
    public ResponseEntity<ApiResponse<List<Review>>> getAllReviewsByProduct(@PathVariable String productId) {
        List<Review> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.<List<Review>>builder()
                .status(true)
                .message("All reviews retrieved")
                .data(reviews)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reviews/{reviewId}/reply")
    public ResponseEntity<ApiResponse<Review>> replyToReview(
            @PathVariable String reviewId,
            @RequestBody String replyContent,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Review updatedReview = reviewService.replyToReview(reviewId, replyContent, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.<Review>builder()
                .status(true)
                .message("Reply added to review")
                .data(updatedReview)
                .build());
    }

}