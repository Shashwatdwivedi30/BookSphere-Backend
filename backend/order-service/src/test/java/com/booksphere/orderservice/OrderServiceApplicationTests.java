package com.booksphere.orderservice;

import com.booksphere.orderservice.controller.OrderController;
import com.booksphere.orderservice.dto.OrderDTO;
import com.booksphere.orderservice.model.Order;
import com.booksphere.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
class OrderServiceApplicationTests {

    @Autowired
    private OrderController orderController;

    @Autowired
    private OrderService orderService;

    @MockBean
    private com.booksphere.orderservice.repository.OrderRepository orderRepository;

    @Test
    void contextLoads() {
        assertThat(orderController).isNotNull();
        assertThat(orderService).isNotNull();
    }

    @Test
    void testOrderModelCreation() {
        Order order = new Order();
        order.setUserId("user1");
        order.setTotalPrice(100.0);
        order.setOrderStatus("PENDING");
        assertThat(order.getUserId()).isEqualTo("user1");
    }

    @Test
    void testOrderServiceBeanExists() {
        assertThat(orderService).isNotNull();
    }

    @Test
    void testOrderControllerBeanExists() {
        assertThat(orderController).isNotNull();
    }

    @Test
    void testGetUserOrdersIntegration() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        
        when(orderRepository.findByUserId("user1")).thenReturn(orders);
        
        List<OrderDTO> result = orderController.getOrdersByUser("user1");
        assertThat(result).hasSize(1);
    }

    @Test
    void testOrderStatusField() {
        Order order = new Order();
        order.setOrderStatus("DELIVERED");
        assertThat(order.getOrderStatus()).isEqualTo("DELIVERED");
    }

    @Test
    void testOrderTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order();
        order.setCreatedAt(now);
        assertThat(order.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testOrderAddressField() {
        Order order = new Order();
        com.booksphere.orderservice.model.Address address = new com.booksphere.orderservice.model.Address();
        address.setFullAddress("123 Street");
        order.setShippingAddress(address);
        assertThat(order.getShippingAddress().getFullAddress()).isEqualTo("123 Street");
    }

    @Test
    void testOrderTotalCalculation() {
        Order order = new Order();
        order.setTotalPrice(1500.0);
        assertThat(order.getTotalPrice()).isPositive();
    }
}
