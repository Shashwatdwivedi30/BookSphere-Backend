package com.booksphere.reviewservice.service;

import com.booksphere.reviewservice.model.Review;
import com.booksphere.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void testAddReview() {
        Review review = Review.builder().bookId("b1").build();
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.addReview(review);

        assertEquals("b1", result.getBookId());
    }

    @Test
    void testAddReview_Update() {
        Review review = Review.builder().bookId("b1").userId("u1").rating(5).build();
        Review existing = Review.builder().bookId("b1").userId("u1").rating(4).build();
        when(reviewRepository.findByBookIdAndUserId("b1", "u1")).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenReturn(existing);

        Review result = reviewService.addReview(review);

        assertEquals(5, result.getRating());
    }

    @Test
    void testGetAverageRatingForBook_Empty() {
        when(reviewRepository.findByBookId("b2")).thenReturn(java.util.Collections.emptyList());
        Double result = reviewService.getAverageRatingForBook("b2");
        assertEquals(0.0, result);
    }

    @Test
    void testGetAverageRatingForBook_WithRatings() {
        Review r1 = Review.builder().rating(5).build();
        Review r2 = Review.builder().rating(3).build();
        Review r3 = Review.builder().rating(null).build();
        when(reviewRepository.findByBookId("b1")).thenReturn(java.util.Arrays.asList(r1, r2, r3, null));

        Double result = reviewService.getAverageRatingForBook("b1");

        assertEquals(2.6666666666666665, result);
    }

    @Test
    void testGetAllReviews() {
        reviewService.getAllReviews();
        org.mockito.Mockito.verify(reviewRepository).findAll();
    }

    @Test
    void testGetReviewsForBook() {
        Review review = Review.builder().bookId("b1").build();
        when(reviewRepository.findByBookId("b1")).thenReturn(java.util.Collections.singletonList(review));

        List<Review> result = reviewService.getReviewsForBook("b1");

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteReview() {
        reviewService.deleteReview("r1");
        org.mockito.Mockito.verify(reviewRepository).deleteById("r1");
    }
}
