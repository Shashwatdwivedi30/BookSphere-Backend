package com.booksphere.notificationservice.consumer;

import com.booksphere.notificationservice.dto.NotificationEvent;
import com.booksphere.notificationservice.model.Notification;
import com.booksphere.notificationservice.service.EmailService;
import com.booksphere.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private RabbitMQConsumer rabbitMQConsumer;

    @Test
    void testConsume_AllSuccess() {
        NotificationEvent event = new NotificationEvent();
        event.setUserId("u1");
        event.setMessage("Test message");
        event.setType("ORDER_PLACED");
        event.setUserEmail("test@example.com");
        event.setUserName("Test User");

        Notification notification = new Notification();
        when(notificationService.sendNotification(any())).thenReturn(notification);

        rabbitMQConsumer.consume(event);

        verify(notificationService).sendNotification(any());
        verify(messagingTemplate).convertAndSend(anyString(), any(Notification.class));
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testConsume_WebSocketAndEmailFailure() {
        NotificationEvent event = new NotificationEvent();
        event.setUserId("u1");
        event.setType("PASSWORD_RESET");

        when(notificationService.sendNotification(any())).thenReturn(new Notification());
        doThrow(new RuntimeException("WS Down")).when(messagingTemplate).convertAndSend(anyString(), any(Notification.class));
        doThrow(new RuntimeException("Email Down")).when(emailService).sendEmail(any(), any(), any());

        rabbitMQConsumer.consume(event);

        verify(notificationService).sendNotification(any());
        // Should not throw exception to caller
    }

    @Test
    void testConsume_OrderPaidAndCancelledTypes() {
        NotificationEvent event = new NotificationEvent();
        event.setType("ORDER_PAID");
        when(notificationService.sendNotification(any())).thenReturn(new Notification());
        rabbitMQConsumer.consume(event);

        event.setType("ORDER_CANCELLED");
        rabbitMQConsumer.consume(event);

        event.setType("UNKNOWN");
        rabbitMQConsumer.consume(event);
        
        event.setType(null);
        rabbitMQConsumer.consume(event);

        verify(notificationService, times(4)).sendNotification(any());
    }

    @Test
    void testConsume_MajorFailure() {
        NotificationEvent event = new NotificationEvent();
        when(notificationService.sendNotification(any())).thenThrow(new RuntimeException("DB Down"));
        rabbitMQConsumer.consume(event);
        // Should catch and log
    }
}
