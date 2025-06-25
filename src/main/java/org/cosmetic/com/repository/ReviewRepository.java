package org.cosmetic.com.repository;

import org.cosmetic.com.enums.ReviewStatus;
import org.cosmetic.com.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByProductIdAndReviewStatus(String productId, ReviewStatus reviewStatus);
    List<Review> findByProductId(String productId);
}