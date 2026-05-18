package com.booksphere.cartservice.model;

import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private String bookId;
    private String title;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private String isbn;
}