package com.booksphere.wishlistservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {
    private String bookId;
    private String title;
    private String author;
    private Double price;
    private String imageUrl;
    private String isbn;
    private LocalDateTime addedAt = LocalDateTime.now();
}
