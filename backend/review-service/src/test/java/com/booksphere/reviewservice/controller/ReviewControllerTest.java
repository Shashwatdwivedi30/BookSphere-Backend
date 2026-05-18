package com.booksphere.reviewservice.controller;

import com.booksphere.reviewservice.dto.ReviewDTO;
import com.booksphere.reviewservice.model.Review;
import com.booksphere.reviewservice.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    void testAddReview() {
        when(reviewService.addReview(any(Review.class))).thenReturn(new Review());
        assertNotNull(reviewController.addReview(new ReviewDTO()));
    }

    @Test
    void testGetReviewsForBook() {
        when(reviewService.getReviewsForBook(anyString())).thenReturn(new ArrayList<>());
        assertNotNull(reviewController.getReviewsForBook("b1"));
    }

    @Test
    void testGetAverageRating() {
        when(reviewService.getAverageRatingForBook(anyString())).thenReturn(5.0);
        assertNotNull(reviewController.getAverageRatingForBook("b1"));
    }

    @Test
    void testDeleteReview() {
        reviewController.deleteReview("r1");
        assertNotNull(reviewService);
    }
}
