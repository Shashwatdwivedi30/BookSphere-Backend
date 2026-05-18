package com.booksphere.orderservice.service;

import com.booksphere.orderservice.model.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(Order order);
    Order payOrder(String orderId, String modeOfPayment, Double amount);
    List<Order> getOrdersByUser(String userId);
    List<Order> getAllOrders();
    Order updateOrderStatus(String orderId, String status);
}
