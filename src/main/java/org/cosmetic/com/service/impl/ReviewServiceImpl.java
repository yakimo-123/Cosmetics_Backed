package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.ReviewRequestDto;
import org.cosmetic.com.enums.ReviewStatus;
import org.cosmetic.com.mapper.ReviewMapper;
import org.cosmetic.com.model.Reply;
import org.cosmetic.com.model.Review;
import org.cosmetic.com.repository.ReviewRepository;
import org.cosmetic.com.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public Review createReview(ReviewRequestDto requestDto) {
        Review review = reviewMapper.toEntity(requestDto);
        review.setCreatedAt(Instant.now());
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getApprovedReviewsByProduct(String productId) {
        return reviewRepository.findByProductIdAndReviewStatus(productId, ReviewStatus.APPROVED);
    }

    @Override
    public Review updateReviewStatus(String reviewId, ReviewStatus status) {
        return reviewRepository.findById(reviewId)
                .map(review -> {
                    review.setReviewStatus(status);
                    return reviewRepository.save(review);
                })
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));
    }

    @Override
    public List<Review> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public Review replyToReview(String reviewId, String replyContent, String userNameAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));
        if(review.getReply() != null) {
            throw new IllegalArgumentException("Review already has a reply");
        }
        // Create a new reply
        Reply reply = new Reply(userNameAdmin, replyContent, Instant.now());
        review.setReply(reply);
        review.setReviewStatus(ReviewStatus.REPLIED);
        return reviewRepository.save(review);
    }
}
