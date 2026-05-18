package com.booksphere.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String bookId;
    private String title;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private String isbn;
}
