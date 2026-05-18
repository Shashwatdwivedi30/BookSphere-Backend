package com.booksphere.reviewservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    
    @NotBlank(message = "Book ID is required")
    private String bookId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Rating is required")
    @Min(1) @Max(5)
    private Integer rating;
    
    @NotBlank(message = "Comment is required")
    private String comment;
    
    private LocalDateTime createdAt;
}
