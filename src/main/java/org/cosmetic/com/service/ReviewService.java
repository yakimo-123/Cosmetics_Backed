package org.cosmetic.com.service;

import org.cosmetic.com.enums.ReviewStatus;
import org.cosmetic.com.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(Review review);
    List<Review> getApprovedReviewsByProduct(String productId);
    Review updateReviewStatus(String reviewId, ReviewStatus status);
    List<Review> getReviewsByProduct(String productId);

}
