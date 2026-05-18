package com.booksphere.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private String bookId;
    private String title;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private String isbn;
}
