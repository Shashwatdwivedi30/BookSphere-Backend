package com.booksphere.notificationservice.controller;

import com.booksphere.notificationservice.dto.NotificationDTO;
import com.booksphere.notificationservice.model.Notification;
import com.booksphere.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public NotificationDTO sendNotification(@RequestBody NotificationDTO notificationDto) {
        return convertToDTO(notificationService.sendNotification(convertToEntity(notificationDto)));
    }

    @GetMapping("/user/{userId:.+}")
    public List<NotificationDTO> getUserNotifications(@PathVariable String userId) {
        return notificationService.getUserNotifications(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/read/{id}")
    public NotificationDTO markAsRead(@PathVariable String id) {
        return convertToDTO(notificationService.markAsRead(id));
    }

    @PutMapping("/read-all/{userId:.+}")
    public void markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        if (notification == null) return null;
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .timestamp(notification.getTimestamp())
                .build();
    }

    private Notification convertToEntity(NotificationDTO dto) {
        if (dto == null) return null;
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setUserId(dto.getUserId());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notification.setIsRead(dto.getIsRead());
        notification.setTimestamp(dto.getTimestamp());
        return notification;
    }
}
