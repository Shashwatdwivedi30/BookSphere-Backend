package com.booksphere.reviewservice.service;

import com.booksphere.reviewservice.model.Review;
import com.booksphere.reviewservice.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review addReview(Review review) {
        log.info("Adding/Updating review for book {} by user {}", review.getBookId(), review.getUserId());
        // Enforce one review per user per book
        Optional<Review> existingReview = reviewRepository.findByBookIdAndUserId(review.getBookId(), review.getUserId());
        if (existingReview.isPresent()) {
            Review revToUpdate = existingReview.get();
            revToUpdate.setRating(review.getRating());
            revToUpdate.setComment(review.getComment());
            revToUpdate.setCreatedAt(LocalDateTime.now());
            return reviewRepository.save(revToUpdate);
        }
        
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getReviewsForBook(String bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    @Override
    public Double getAverageRatingForBook(String bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
                .filter(Objects::nonNull)
                .mapToDouble(r -> r.getRating() != null ? r.getRating() : 0.0)
                .average()
                .orElse(0.0);
    }

    @Override
    public void deleteReview(String id) {
        log.info("Deleting review with id {}", id);
        reviewRepository.deleteById(id);
    }
}
