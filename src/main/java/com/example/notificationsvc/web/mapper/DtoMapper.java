package com.example.notificationsvc.web.mapper;

import lombok.experimental.UtilityClass;
import com.example.notificationsvc.model.Notification;
import com.example.notificationsvc.model.NotificationPreference;
import com.example.notificationsvc.web.dto.NotificationPreferenceResponse;
import com.example.notificationsvc.web.dto.NotificationResponse;

@UtilityClass
public class DtoMapper {

    public NotificationPreferenceResponse toNotificationPreferenceResponse(NotificationPreference notificationPreference) {
        return NotificationPreferenceResponse.
                builder().
                id(notificationPreference.getId()).
                userId(notificationPreference.getUserId()).
                enabled(notificationPreference.isEnabled()).
                build();
    }

    public NotificationResponse toNotificationRequest(Notification notification) {
        return NotificationResponse.builder()
                .subject(notification.getSubject())
                .createdAt(notification.getCreatedAt())
                .status(notification.getStatus())
                .build();
    }
}
