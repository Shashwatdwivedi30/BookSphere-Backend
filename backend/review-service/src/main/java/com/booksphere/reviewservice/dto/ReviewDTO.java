package com.booksphere.reviewservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    @NotBlank(message = "Associated Book ID must be provided")
    private String bookId;

    private String id;

    @NotBlank(message = "User who wrote the review is required")
    private String userId;

    @NotNull(message = "Star rating is required")
    @Min(value = 1, message = "Minimum rating is 1 star")
    @Max(value = 5, message = "Maximum rating is 5 stars")
    private Integer rating;

    private LocalDateTime createdAt;

    @NotBlank(message = "Review content comment cannot be empty")
    private String comment;
}
