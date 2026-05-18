package com.booksphere.orderservice.service;

import com.booksphere.orderservice.dto.NotificationEvent;
import com.booksphere.orderservice.exception.OrderException;
import com.booksphere.orderservice.model.Order;
import com.booksphere.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void testGetOrdersByUser() {
        Order order = new Order();
        order.setUserId("u1");
        when(orderRepository.findByUserId("u1")).thenReturn(java.util.Collections.singletonList(order));

        java.util.List<Order> result = orderService.getOrdersByUser("u1");

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateOrderStatus() {
        Order order = new Order();
        order.setOrderId("o1");
        order.setOrderStatus("PENDING");
        
        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus("o1", "PAID");

        assertEquals("PAID", result.getOrderStatus());
    }

    @Test
    void testPlaceOrder_Success() {
        Order order = new Order();
        order.setOrderId("o123456789");
        order.setUserId("u1");
        com.booksphere.orderservice.model.OrderItem item = new com.booksphere.orderservice.model.OrderItem();
        item.setBookId("b1");
        item.setQuantity(2);
        order.setItems(java.util.Collections.singletonList(item));

        java.util.Map<String, Object> bookResp = new java.util.HashMap<>();
        bookResp.put("stock", 10);
        
        when(restTemplate.getForObject(anyString(), eq(java.util.Map.class))).thenReturn(bookResp);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(order);

        assertEquals("PENDING", result.getOrderStatus());
        verify(rabbitTemplate).convertAndSend(any(), any(), any(com.booksphere.orderservice.dto.NotificationEvent.class));
    }

    @Test
    void testPlaceOrder_InsufficientStock() {
        Order order = new Order();
        com.booksphere.orderservice.model.OrderItem item = new com.booksphere.orderservice.model.OrderItem();
        item.setBookId("b1");
        item.setQuantity(10);
        order.setItems(java.util.Collections.singletonList(item));

        java.util.Map<String, Object> bookResp = new java.util.HashMap<>();
        bookResp.put("stock", 5);
        
        when(restTemplate.getForObject(anyString(), eq(java.util.Map.class))).thenReturn(bookResp);

        org.junit.jupiter.api.Assertions.assertThrows(OrderException.class, () -> orderService.placeOrder(order));
    }

    @Test
    void testPlaceOrder_StockFetchError() {
        Order order = new Order();
        com.booksphere.orderservice.model.OrderItem item = new com.booksphere.orderservice.model.OrderItem();
        item.setBookId("b1");
        item.setQuantity(1);
        order.setItems(java.util.Collections.singletonList(item));

        // First URL fails, second URL fails
        when(restTemplate.getForObject(anyString(), eq(java.util.Map.class))).thenThrow(new RuntimeException());

        org.junit.jupiter.api.Assertions.assertThrows(OrderException.class, () -> orderService.placeOrder(order));
    }

    @Test
    void testUpdateOrderStatus_Cancelled_Success() {
        Order order = new Order();
        order.setOrderId("o1");
        order.setOrderStatus("PAID");
        com.booksphere.orderservice.model.OrderItem item = new com.booksphere.orderservice.model.OrderItem();
        item.setBookId("b1");
        item.setQuantity(1);
        order.setItems(java.util.Collections.singletonList(item));

        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus("o1", "CANCELLED");

        assertEquals("CANCELLED", result.getOrderStatus());
        verify(restTemplate, atLeastOnce()).exchange(anyString(), any(), any(), eq(Void.class));
    }

    @Test
    void testUpdateOrderStatus_Cancelled_AlreadyCancelled() {
        Order order = new Order();
        order.setOrderId("o1");
        order.setOrderStatus("CANCELLED");

        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus("o1", "CANCELLED");

        assertEquals("CANCELLED", result.getOrderStatus());
        verify(restTemplate, never()).exchange(anyString(), any(), any(), eq(Void.class));
    }

    @Test
    void testUpdateOrderStatus_Cancelled_DeliveredError() {
        Order order = new Order();
        order.setOrderId("o1");
        order.setOrderStatus("DELIVERED");

        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));

        org.junit.jupiter.api.Assertions.assertThrows(OrderException.class, () -> orderService.updateOrderStatus("o1", "CANCELLED"));
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        orderService.getAllOrders();
        verify(orderRepository).findAll();
    }

    @Test
    void testPayOrder_Success() {
        Order order = new Order();
        order.setOrderId("o1");
        when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.payOrder("o1", "CREDIT_CARD", 100.0);

        assertEquals("PAID", result.getOrderStatus());
        assertEquals("CREDIT_CARD", result.getModeOfPayment());
    }
}
