package com.booksphere.orderservice.service;

import com.booksphere.orderservice.dto.NotificationEvent;
import com.booksphere.orderservice.exception.OrderException;
import com.booksphere.orderservice.exception.OrderNotFoundException;
import com.booksphere.orderservice.model.Order;
import com.booksphere.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_DELIVERED = "DELIVERED";
    private static final String ORDER_NOT_FOUND = "Order not found";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private static final List<String> BOOK_SERVICE_BASE_URLS = Arrays.asList(
            "http://book-service",
            "http://localhost:8081"
    );

    @Override
    public Order placeOrder(Order order) {
        if (order.getItems() != null) {
            order.getItems().forEach(item -> {
                Integer stock = fetchBookStock(item.getBookId());
                if (stock == null) {
                    throw new OrderException("Unable to verify stock for book: " + item.getTitle());
                }
                if (stock < item.getQuantity()) {
                    throw new OrderException("Insufficient stock for " + item.getTitle() + ". Available: " + stock);
                }
            });
        }

        if (order.getOrderStatus() == null || order.getOrderStatus().isEmpty()) {
            order.setOrderStatus("PENDING");
        }
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Reduce stock for each item
        if (order.getItems() != null) {
            order.getItems().forEach(item -> {
                log.info("Reducing stock for book: {} by {}", item.getBookId(), item.getQuantity());
                callBookServicePut("/books/reduce-stock/" + item.getBookId() + "/" + item.getQuantity(),
                        "reduce stock for book " + item.getBookId());
            });
        }
        
        sendNotification(savedOrder, "ORDER_PLACED");
        return savedOrder;
    }

    @Override
    public Order payOrder(String orderId, String modeOfPayment, Double amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        
        // Mode of payment WALLET integration will be added here when WalletService is ready
        
        order.setModeOfPayment(modeOfPayment);
        order.setTotalPrice(amount);
        order.setOrderStatus(STATUS_PAID);
        Order savedOrder = orderRepository.save(order);
        sendNotification(savedOrder, "ORDER_PAID");
        return savedOrder;
    }

    @Override
    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrderStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        if (STATUS_CANCELLED.equalsIgnoreCase(status)) {
            handleCancellation(order);
        }

        order.setOrderStatus(status);
        Order savedOrder = orderRepository.save(order);
        if (STATUS_CANCELLED.equalsIgnoreCase(status)) {
            sendNotification(savedOrder, "ORDER_CANCELLED");
        }
        return savedOrder;
    }

    private void handleCancellation(Order order) {
        String currentStatus = order.getOrderStatus() == null ? "" : order.getOrderStatus().toUpperCase();
        if (STATUS_CANCELLED.equals(currentStatus)) {
            return;
        }
        if (STATUS_DELIVERED.equals(currentStatus)) {
            throw new OrderException("Delivered orders cannot be cancelled");
        }
        if (order.getItems() != null) {
            order.getItems().forEach(item -> {
                try {
                    log.info("Restoring stock for cancelled book: {} by {}", item.getBookId(), item.getQuantity());
                    callBookServicePut("/books/increase-stock/" + item.getBookId() + "/" + item.getQuantity(),
                            "restore stock for book " + item.getBookId());
                    log.info("Successfully restored stock for book: {}", item.getBookId());
                } catch (Exception e) {
                    log.error("Failed to restore stock for book: {}. Reason: {}", item.getBookId(), e.getMessage());
                }
            });
        }
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private void sendNotification(Order order, String type) {
        String shortId = order.getOrderId().length() > 8 
            ? order.getOrderId().substring(order.getOrderId().length() - 8) 
            : order.getOrderId();
        
        String message = getNotificationMessage(type, shortId);

        NotificationEvent event = NotificationEvent.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .userName(order.getShippingAddress() != null ? order.getShippingAddress().getFullName() : "Reader")
                .userEmail(order.getUserId()) // Based on frontend, userId IS the email
                .message(message)
                .type(type)
                .amount(order.getTotalPrice())
                .build();

        try {
            log.info("Sending notification event to RabbitMQ: {}", event);
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (Exception e) {
            log.error("Failed to send notification to RabbitMQ: {}", e.getMessage());
        }
    }

    private String getNotificationMessage(String type, String shortId) {
        switch (type) {
            case "ORDER_PLACED":
                return "Your order #" + shortId + " has been placed successfully!";
            case "ORDER_PAID":
                return "Payment received for order #" + shortId + ". We are preparing your books!";
            case "ORDER_CANCELLED":
                return "Your order #" + shortId + " has been cancelled.";
            default:
                return "Notification for order #" + shortId;
        }
    }

    private Integer fetchBookStock(String bookId) {
        Exception last = null;
        for (String baseUrl : BOOK_SERVICE_BASE_URLS) {
            try {
                Map<String, Object> book = restTemplate.getForObject(baseUrl + "/books/" + bookId, Map.class);
                if (book == null || book.get("stock") == null) {
                    return null;
                }
                return ((Number) book.get("stock")).intValue();
            } catch (Exception ex) {
                last = ex;
            }
        }
        if (last != null) {
            log.error("Failed to fetch stock for book: {}. Reason: {}", bookId, last.getMessage());
        }
        return null;
    }

    private void callBookServicePut(String path, String actionLabel) {
        Exception last = null;
        for (String baseUrl : BOOK_SERVICE_BASE_URLS) {
            try {
                String url = baseUrl + path;
                restTemplate.exchange(url, HttpMethod.PUT, HttpEntity.EMPTY, Void.class);
                return;
            } catch (Exception ex) {
                last = ex;
            }
        }
        throw new OrderException("Failed to " + actionLabel + " (book-service unreachable): " + (last != null ? last.getMessage() : "Unknown error"));
    }
}
