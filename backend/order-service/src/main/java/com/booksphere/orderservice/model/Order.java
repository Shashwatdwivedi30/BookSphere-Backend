package com.booksphere.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String orderId;
    private String userId;
    private List<OrderItem> items;
    private Double totalPrice;
    private String orderStatus; // PLACED, PAID, DELIVERED, CANCELLED
    private String modeOfPayment; // WALLET, COD, UPI, CARD
    private Address shippingAddress;
    private LocalDateTime createdAt;
}
