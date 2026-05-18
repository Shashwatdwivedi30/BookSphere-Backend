package com.booksphere.reviewservice.controller;

import com.booksphere.reviewservice.dto.ReviewDTO;
import com.booksphere.reviewservice.model.Review;
import com.booksphere.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ReviewDTO addReview(@Valid @RequestBody ReviewDTO reviewDto) {
        return convertToDTO(reviewService.addReview(convertToEntity(reviewDto)));
    }

    @GetMapping("/book/{bookId}")
    public List<ReviewDTO> getReviewsForBook(@PathVariable String bookId) {
        return reviewService.getReviewsForBook(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ReviewDTO> getAllReviews() {
        return reviewService.getAllReviews().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/average/{bookId}")
    public Double getAverageRatingForBook(@PathVariable String bookId) {
        return reviewService.getAverageRatingForBook(bookId);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
    }

    private ReviewDTO convertToDTO(Review review) {
        if (review == null) return null;
        return ReviewDTO.builder()
                .id(review.getId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private Review convertToEntity(ReviewDTO dto) {
        if (dto == null) return null;
        Review review = new Review();
        review.setId(dto.getId());
        review.setBookId(dto.getBookId());
        review.setUserId(dto.getUserId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(dto.getCreatedAt());
        return review;
    }
}
