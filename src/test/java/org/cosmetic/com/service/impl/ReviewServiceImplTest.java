package org.cosmetic.com.service.impl;

import org.cosmetic.com.dto.request.ReviewRequestDto;
import org.cosmetic.com.enums.ReviewStatus;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.ReviewMapper;
import org.cosmetic.com.model.Reply;
import org.cosmetic.com.model.Review;
import org.cosmetic.com.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review createReview(String id, String content) {
        Review review = new Review();
        review.setId(id);
        review.setComment(content);
        review.setCreatedAt(Instant.now());
        review.setReviewStatus(ReviewStatus.PENDING);
        return review;
    }

    @Nested
    @DisplayName("Create Review Tests")
    class CreateReviewTests {

        @Test
        @DisplayName("Should successfully create review")
        void shouldSuccessfullyCreateReview() {
            // Given
            ReviewRequestDto requestDto = new ReviewRequestDto();
            Review review = createReview("1", "Test review");
            when(reviewMapper.toEntity(requestDto)).thenReturn(review);
            when(reviewRepository.save(any(Review.class))).thenReturn(review);

            // When
            Review createdReview = reviewService.createReview(requestDto);

            // Then
            assertNotNull(createdReview);
            assertNotNull(createdReview.getCreatedAt());
            verify(reviewMapper).toEntity(requestDto);
            verify(reviewRepository).save(any(Review.class));
        }
    }

    @Nested
    @DisplayName("Get Approved Reviews Tests")
    class GetApprovedReviewsTests {

        @Test
        @DisplayName("Should return approved reviews for product")
        void shouldReturnApprovedReviewsForProduct() {
            // Given
            String productId = "1";
            List<Review> expectedReviews = Arrays.asList(
                    createReview("1", "Review 1"),
                    createReview("2", "Review 2")
            );
            when(reviewRepository.findByProductIdAndReviewStatus(productId, ReviewStatus.APPROVED))
                    .thenReturn(expectedReviews);

            // When
            List<Review> actualReviews = reviewService.getApprovedReviewsByProduct(productId);

            // Then
            assertEquals(2, actualReviews.size());
            verify(reviewRepository).findByProductIdAndReviewStatus(productId, ReviewStatus.APPROVED);
        }

        @Test
        @DisplayName("Should return empty list when no approved reviews exist")
        void shouldReturnEmptyListWhenNoApprovedReviewsExist() {
            // Given
            String productId = "1";
            when(reviewRepository.findByProductIdAndReviewStatus(productId, ReviewStatus.APPROVED))
                    .thenReturn(Collections.emptyList());

            // When
            List<Review> reviews = reviewService.getApprovedReviewsByProduct(productId);

            // Then
            assertTrue(reviews.isEmpty());
            verify(reviewRepository).findByProductIdAndReviewStatus(productId, ReviewStatus.APPROVED);
        }
    }

    @Nested
    @DisplayName("Update Review Status Tests")
    class UpdateReviewStatusTests {

        @Test
        @DisplayName("Should successfully update review status")
        void shouldSuccessfullyUpdateReviewStatus() {
            // Given
            String reviewId = "1";
            Review review = createReview(reviewId, "Test review");
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);

            // When
            Review updatedReview = reviewService.updateReviewStatus(reviewId, ReviewStatus.APPROVED);

            // Then
            assertNotNull(updatedReview);
            assertEquals(ReviewStatus.APPROVED, updatedReview.getReviewStatus());
            verify(reviewRepository).findById(reviewId);
            verify(reviewRepository).save(review);
        }

        @Test
        @DisplayName("Should throw exception when review not found")
        void shouldThrowExceptionWhenReviewNotFound() {
            // Given
            String reviewId = "1";
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> reviewService.updateReviewStatus(reviewId, ReviewStatus.APPROVED));
            assertEquals(ErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
            verify(reviewRepository).findById(reviewId);
            verify(reviewRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Reviews By Product Tests")
    class GetReviewsByProductTests {

        @Test
        @DisplayName("Should return all reviews for product")
        void shouldReturnAllReviewsForProduct() {
            // Given
            String productId = "1";
            List<Review> expectedReviews = Arrays.asList(
                    createReview("1", "Review 1"),
                    createReview("2", "Review 2")
            );
            when(reviewRepository.findByProductId(productId)).thenReturn(expectedReviews);

            // When
            List<Review> actualReviews = reviewService.getReviewsByProduct(productId);

            // Then
            assertEquals(2, actualReviews.size());
            verify(reviewRepository).findByProductId(productId);
        }

        @Test
        @DisplayName("Should return empty list when no reviews exist")
        void shouldReturnEmptyListWhenNoReviewsExist() {
            // Given
            String productId = "1";
            when(reviewRepository.findByProductId(productId)).thenReturn(Collections.emptyList());

            // When
            List<Review> reviews = reviewService.getReviewsByProduct(productId);

            // Then
            assertTrue(reviews.isEmpty());
            verify(reviewRepository).findByProductId(productId);
        }
    }

    @Nested
    @DisplayName("Reply To Review Tests")
    class ReplyToReviewTests {

        @Test
        @DisplayName("Should successfully reply to review")
        void shouldSuccessfullyReplyToReview() {
            // Given
            String reviewId = "1";
            String replyContent = "Test reply";
            String adminUsername = "admin";
            Review review = createReview(reviewId, "Test review");
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);

            // When
            Review updatedReview = reviewService.replyToReview(reviewId, replyContent, adminUsername);

            // Then
            assertNotNull(updatedReview);
            assertNotNull(updatedReview.getReply());
            assertEquals(replyContent, updatedReview.getReply().getContent());
            assertEquals(adminUsername, updatedReview.getReply().getUserNameAdmin());
            assertEquals(ReviewStatus.REPLIED, updatedReview.getReviewStatus());
            verify(reviewRepository).findById(reviewId);
            verify(reviewRepository).save(review);
        }

        @Test
        @DisplayName("Should throw exception when review already has reply")
        void shouldThrowExceptionWhenReviewAlreadyHasReply() {
            // Given
            String reviewId = "1";
            Review review = createReview(reviewId, "Test review");
            review.setReply(new Reply("admin", "Existing reply", Instant.now()));
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> reviewService.replyToReview(reviewId, "New reply", "admin"));
            assertEquals(ErrorCode.REVIEW_ALREADY_REPLIED, exception.getErrorCode());
            verify(reviewRepository).findById(reviewId);
            verify(reviewRepository, never()).save(any());
        }
    }
}