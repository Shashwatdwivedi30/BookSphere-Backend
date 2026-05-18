package com.booksphere.notificationservice.consumer;

import com.booksphere.notificationservice.dto.NotificationEvent;
import com.booksphere.notificationservice.model.Notification;
import com.booksphere.notificationservice.service.EmailService;
import com.booksphere.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQConsumer {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(NotificationEvent event) {
        log.info("Received notification event from RabbitMQ: {}", event);

        try {
            // 1. Save to Database
            Notification notification = new Notification();
            notification.setUserId(event.getUserId());
            notification.setMessage(event.getMessage());
            notification.setType(event.getType());
            Notification savedNotification = notificationService.sendNotification(notification);

            // 2. Push via WebSockets for Real-time UI
            pushToWebsocket(event, savedNotification);

            // 3. Send Email (Optional - don't crash if it fails)
            sendEmailIfPossible(event);
            
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage());
        }
    }

    private void pushToWebsocket(NotificationEvent event, Notification savedNotification) {
        try {
            messagingTemplate.convertAndSend("/topic/notifications/" + event.getUserId(), savedNotification);
        } catch (Exception wsEx) {
            log.warn("WebSocket push failed: {}", wsEx.getMessage());
        }
    }

    private void sendEmailIfPossible(NotificationEvent event) {
        try {
            String userName = event.getUserName() != null ? event.getUserName() : "Reader";
            String subject = getSubject(event.getType());
            String personalizedMessage = "Hello " + userName + ",\n\n" + event.getMessage();
            emailService.sendEmail(event.getUserEmail(), subject, personalizedMessage);
        } catch (Exception emailEx) {
            log.warn("Email sending failed: {}", emailEx.getMessage());
        }
    }

    private String getSubject(String type) {
        if (type == null) return "BookSphere Notification";
        switch (type) {
            case "ORDER_PLACED":
                return "BookSphere: Order Placed Successfully";
            case "ORDER_PAID":
                return "BookSphere: Payment Received";
            case "ORDER_CANCELLED":
                return "BookSphere: Order Cancelled";
            case "PASSWORD_RESET":
                return "BookSphere: Password Reset OTP";
            default:
                return "BookSphere Notification";
        }
    }
}
