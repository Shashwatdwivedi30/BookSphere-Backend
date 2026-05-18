package com.booksphere.reviewservice.service;

import com.booksphere.reviewservice.model.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);
    List<Review> getReviewsForBook(String bookId);
    List<Review> getAllReviews();
    Double getAverageRatingForBook(String bookId);
    void deleteReview(String reviewId);
}
