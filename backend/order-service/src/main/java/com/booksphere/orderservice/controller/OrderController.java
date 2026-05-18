package com.booksphere.orderservice.controller;

import com.booksphere.orderservice.dto.OrderDTO;
import com.booksphere.orderservice.dto.OrderItemDTO;
import com.booksphere.orderservice.dto.AddressDTO;
import com.booksphere.orderservice.model.Order;
import com.booksphere.orderservice.model.OrderItem;
import com.booksphere.orderservice.model.Address;
import com.booksphere.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public OrderDTO placeOrder(@RequestBody OrderDTO orderDto) {
        return convertToDTO(orderService.placeOrder(convertToEntity(orderDto)));
    }

    @PostMapping("/pay")
    public OrderDTO payOrder(@RequestParam String orderId, @RequestParam String modeOfPayment, @RequestParam Double amount) {
        return convertToDTO(orderService.payOrder(orderId, modeOfPayment, amount));
    }

    @GetMapping("/user/{userId:.+}")
    public List<OrderDTO> getOrdersByUser(@PathVariable String userId) {
        return orderService.getOrdersByUser(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/status")
    public OrderDTO updateOrderStatus(@RequestParam String orderId, @RequestParam String status) {
        return convertToDTO(orderService.updateOrderStatus(orderId, status));
    }

    @GetMapping("/all")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        if (order == null) return null;
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .items(order.getItems() != null ? order.getItems().stream()
                        .map(this::convertItemToDTO)
                        .collect(Collectors.toList()) : null)
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus())
                .modeOfPayment(order.getModeOfPayment())
                .shippingAddress(convertAddressToDTO(order.getShippingAddress()))
                .createdAt(order.getCreatedAt())
                .build();
    }

    private Order convertToEntity(OrderDTO dto) {
        if (dto == null) return null;
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setUserId(dto.getUserId());
        order.setItems(dto.getItems() != null ? dto.getItems().stream()
                .map(this::convertItemToEntity)
                .collect(Collectors.toList()) : null);
        order.setTotalPrice(dto.getTotalPrice());
        order.setOrderStatus(dto.getOrderStatus());
        order.setModeOfPayment(dto.getModeOfPayment());
        order.setShippingAddress(convertAddressToEntity(dto.getShippingAddress()));
        order.setCreatedAt(dto.getCreatedAt());
        return order;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        if (item == null) return null;
        return OrderItemDTO.builder()
                .bookId(item.getBookId())
                .title(item.getTitle())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .imageUrl(item.getImageUrl())
                .isbn(item.getIsbn())
                .build();
    }

    private OrderItem convertItemToEntity(OrderItemDTO dto) {
        if (dto == null) return null;
        OrderItem item = new OrderItem();
        item.setBookId(dto.getBookId());
        item.setTitle(dto.getTitle());
        item.setPrice(dto.getPrice());
        item.setQuantity(dto.getQuantity());
        item.setImageUrl(dto.getImageUrl());
        item.setIsbn(dto.getIsbn());
        return item;
    }

    private AddressDTO convertAddressToDTO(Address address) {
        if (address == null) return null;
        return AddressDTO.builder()
                .id(address.getId())
                .orderCustomerId(address.getCustomerId())
                .receiverName(address.getFullName())
                .receiverMobile(address.getMobileNumber())
                .completeAddress(address.getFullAddress())
                .cityName(address.getCity())
                .stateName(address.getState())
                .postalCode(address.getPincode())
                .build();
    }

    private Address convertAddressToEntity(AddressDTO dto) {
        if (dto == null) return null;
        Address address = new Address();
        address.setId(dto.getId());
        address.setCustomerId(dto.getOrderCustomerId());
        address.setFullName(dto.getReceiverName());
        address.setMobileNumber(dto.getReceiverMobile());
        address.setFullAddress(dto.getCompleteAddress());
        address.setCity(dto.getCityName());
        address.setState(dto.getStateName());
        address.setPincode(dto.getPostalCode());
        return address;
    }
}
