package com.booksphere.notificationservice;

import com.booksphere.notificationservice.controller.NotificationController;
import com.booksphere.notificationservice.dto.NotificationDTO;
import com.booksphere.notificationservice.model.Notification;
import com.booksphere.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
class NotificationServiceApplicationTests {

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private com.booksphere.notificationservice.repository.NotificationRepository notificationRepository;

    @Test
    void contextLoads() {
        assertThat(notificationController).isNotNull();
        assertThat(notificationService).isNotNull();
    }

    @Test
    void testNotificationModelCreation() {
        Notification notification = new Notification("1", "user@test.com", "Test Message", "INFO", false, LocalDateTime.now());
        assertThat(notification.getUserId()).isEqualTo("user@test.com");
        assertThat(notification.getMessage()).isEqualTo("Test Message");
    }

    @Test
    void testNotificationServiceBeanExists() {
        assertThat(notificationService).isNotNull();
    }

    @Test
    void testNotificationControllerBeanExists() {
        assertThat(notificationController).isNotNull();
    }

    @Test
    void testSendNotificationIntegration() {
        NotificationDTO noteDto = NotificationDTO.builder()
                .message("Hello")
                .userId("user1")
                .type("INFO")
                .isRead(false)
                .build();
        
        Notification note = new Notification();
        note.setMessage("Hello");
        note.setUserId("user1");
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(note);
        
        NotificationDTO saved = notificationController.sendNotification(noteDto);
        assertThat(saved.getMessage()).isEqualTo("Hello");
    }

    @Test
    void testGetUserNotificationsIntegration() {
        List<Notification> list = new ArrayList<>();
        list.add(new Notification());
        
        when(notificationRepository.findByUserId("user1")).thenReturn(list);
        
        List<NotificationDTO> result = notificationController.getUserNotifications("user1");
        assertThat(result).hasSize(1);
    }

    @Test
    void testMarkAsReadIntegration() {
        Notification note = new Notification();
        note.setId("id1");
        note.setIsRead(true);
        
        when(notificationRepository.findById("id1")).thenReturn(java.util.Optional.of(note));
        when(notificationRepository.save(any(Notification.class))).thenReturn(note);
        
        NotificationDTO result = notificationController.markAsRead("id1");
        assertThat(result.getIsRead()).isTrue();
    }

    @Test
    void testNotificationTypeField() {
        Notification note = new Notification();
        note.setType("ORDER_UPDATE");
        assertThat(note.getType()).isEqualTo("ORDER_UPDATE");
    }

    @Test
    void testNotificationTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        Notification note = new Notification();
        note.setTimestamp(now);
        assertThat(note.getTimestamp()).isEqualTo(now);
    }
}
