package com.booksphere.notificationservice.service;

import com.booksphere.notificationservice.model.Notification;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(Notification notification);
    List<Notification> getUserNotifications(String userId);
    Notification markAsRead(String id);
    void markAllAsRead(String userId);
}
