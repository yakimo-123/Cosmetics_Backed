package org.cosmetic.com.repository;

import org.cosmetic.com.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {
    // Additional query methods can be defined here if needed
}