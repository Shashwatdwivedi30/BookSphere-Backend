package com.booksphere.reviewservice;

import com.booksphere.reviewservice.controller.ReviewController;
import com.booksphere.reviewservice.dto.ReviewDTO;
import com.booksphere.reviewservice.model.Review;
import com.booksphere.reviewservice.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
class ReviewServiceApplicationTests {

    @Autowired
    private ReviewController reviewController;

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private com.booksphere.reviewservice.repository.ReviewRepository reviewRepository;

    @Test
    void contextLoads() {
        assertThat(reviewController).isNotNull();
        assertThat(reviewService).isNotNull();
    }

    @Test
    void testReviewModelCreation() {
        Review review = new Review();
        review.setId("1");
        review.setUserId("user1");
        review.setRating(5);
        review.setComment("Great!");
        assertThat(review.getComment()).isEqualTo("Great!");
    }

    @Test
    void testReviewServiceBeanExists() {
        assertThat(reviewService).isNotNull();
    }

    @Test
    void testReviewControllerBeanExists() {
        assertThat(reviewController).isNotNull();
    }

    @Test
    void testGetBookReviewsIntegration() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review());
        
        when(reviewRepository.findByBookId("book1")).thenReturn(reviews);
        
        List<ReviewDTO> result = reviewController.getReviewsForBook("book1");
        assertThat(result).hasSize(1);
    }

    @Test
    void testReviewRatingRange() {
        Review review = new Review();
        review.setRating(4);
        assertThat(review.getRating()).isBetween(1, 5);
    }

    @Test
    void testReviewCommentField() {
        Review review = new Review();
        review.setComment("Excellent book");
        assertThat(review.getComment()).isNotEmpty();
    }

    @Test
    void testReviewUserIdField() {
        Review review = new Review();
        review.setUserId("test@test.com");
        assertThat(review.getUserId()).contains("@");
    }

    @Test
    void testReviewBookIdField() {
        Review review = new Review();
        review.setBookId("B123");
        assertThat(review.getBookId()).isEqualTo("B123");
    }
}
