package org.cosmetic.com.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String productId;
    private String userId;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
}