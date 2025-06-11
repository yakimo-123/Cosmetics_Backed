package org.cosmetic.com.repository;

import org.cosmetic.com.enums.ReviewStatus;
import org.cosmetic.com.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByProductIdAndReviewStatus(String productId, ReviewStatus reviewStatus);
    List<Review> findByProductId(String productId);
}