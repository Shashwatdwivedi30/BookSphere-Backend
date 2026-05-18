package com.booksphere.bookservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    @NotBlank(message = "Book Title cannot be empty")
    private String title;

    @NotBlank(message = "Book Author cannot be empty")
    private String author;

    private String id;

    @NotBlank(message = "A valid ISBN is required")
    private String isbn;

    @NotBlank(message = "Genre category is required")
    private String genre;

    @NotNull(message = "Price must be specified")
    @Min(value = 1, message = "Price has to be at least 1")
    private Double price;

    private String description;

    @NotNull(message = "Stock count is required")
    @Min(value = 0, message = "Stock amount cannot be negative")
    private Integer stock;

    private String imageUrl;
}
