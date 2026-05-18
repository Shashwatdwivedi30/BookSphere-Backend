package com.booksphere.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private String orderId;
    private String userId;
    private List<OrderItemDTO> items;
    private Double totalPrice;
    private String orderStatus;
    private String modeOfPayment;
    private AddressDTO shippingAddress;
    private LocalDateTime createdAt;
}
