package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.ReviewRequestDto;
import org.cosmetic.com.enums.ReviewStatus;
import org.cosmetic.com.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(ReviewRequestDto review);
    List<Review> getApprovedReviewsByProduct(String productId);
    Review updateReviewStatus(String reviewId, ReviewStatus status);
    List<Review> getReviewsByProduct(String productId);
    Review replyToReview(String reviewId, String replyContent,String userNameAdmin);
}
