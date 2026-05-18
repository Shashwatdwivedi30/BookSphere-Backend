    package com.booksphere.notificationservice.service;

import com.booksphere.notificationservice.model.Notification;
import com.booksphere.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void testSendNotification() {
        Notification notification = new Notification();
        notification.setUserId("u1");
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        Notification result = notificationService.sendNotification(notification);
        assertEquals("u1", result.getUserId());
    }

    @Test
    void testGetNotifications() {
        when(notificationRepository.findByUserId("u1")).thenReturn(Collections.emptyList());
        assertTrue(notificationService.getUserNotifications("u1").isEmpty());
    }

    @Test
    void testMarkAsRead_Found() {
        Notification notification = new Notification();
        notification.setId("n1");
        notification.setIsRead(false);
        when(notificationRepository.findById("n1")).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        Notification result = notificationService.markAsRead("n1");
        assertTrue(result.getIsRead());
    }

    @Test
    void testMarkAsRead_NotFound() {
        when(notificationRepository.findById("n1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificationService.markAsRead("n1"));
    }
}
