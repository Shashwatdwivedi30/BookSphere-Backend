package com.booksphere.orderservice.controller;

import com.booksphere.orderservice.dto.AddressDTO;
import com.booksphere.orderservice.dto.OrderDTO;
import com.booksphere.orderservice.dto.OrderItemDTO;
import com.booksphere.orderservice.model.Address;
import com.booksphere.orderservice.model.Order;
import com.booksphere.orderservice.model.OrderItem;
import com.booksphere.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void testPlaceOrder_Complex() {
        AddressDTO addressDto = AddressDTO.builder()
                .receiverName("Name")
                .completeAddress("Addr")
                .cityName("City")
                .stateName("State")
                .postalCode("123456")
                .build();
        OrderItemDTO itemDto = OrderItemDTO.builder().bookId("b1").price(10.0).quantity(1).build();
        OrderDTO orderDto = OrderDTO.builder()
                .userId("u1")
                .items(Collections.singletonList(itemDto))
                .shippingAddress(addressDto)
                .build();

        Order order = new Order();
        order.setOrderId("o1");
        order.setItems(Collections.singletonList(new OrderItem()));
        order.setShippingAddress(new Address());
        
        when(orderService.placeOrder(any(Order.class))).thenReturn(order);

        assertNotNull(orderController.placeOrder(orderDto));
    }

    @Test
    void testPlaceOrder_Null() {
        assertNull(orderController.placeOrder(null));
    }

    @Test
    void testPayOrder() {
        when(orderService.payOrder(anyString(), anyString(), anyDouble())).thenReturn(new Order());
        assertNotNull(orderController.payOrder("o1", "CARD", 10.0));
    }

    @Test
    void testGetOrdersByUser() {
        when(orderService.getOrdersByUser(anyString())).thenReturn(Collections.singletonList(new Order()));
        assertNotNull(orderController.getOrdersByUser("u1"));
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderService.updateOrderStatus(anyString(), anyString())).thenReturn(new Order());
        assertNotNull(orderController.updateOrderStatus("o1", "CANCELLED"));
    }

    @Test
    void testGetAllOrders() {
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(new Order()));
        assertNotNull(orderController.getAllOrders());
    }
}
