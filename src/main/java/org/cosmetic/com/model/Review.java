package org.cosmetic.com.model;

import lombok.Getter;
import lombok.Setter;
import org.cosmetic.com.enums.ReviewStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;


@Getter
@Setter
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String productId;
    private String userId;
    private String comment;
    private int rating;
    private Instant createdAt;
    private ReviewStatus reviewStatus = ReviewStatus.PENDING;
    private Reply reply;
}