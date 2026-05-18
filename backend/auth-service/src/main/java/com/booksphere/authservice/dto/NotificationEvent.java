package com.booksphere.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {
    private String orderId;
    private String userId;
    private String userName;
    private String userEmail;
    private String message;
    private String type; // ORDER_PLACED, ORDER_PAID, ORDER_CANCELLED, PASSWORD_RESET
    private Double amount;
}
