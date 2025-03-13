package web.mapper;

import lombok.experimental.UtilityClass;
import model.Notification;
import model.NotificationPreference;
import model.NotificationStatus;
import web.dto.NotificationPreferenceResponse;
import web.dto.NotificationRequest;
import web.dto.NotificationResponse;

@UtilityClass
public class DtoMapper {

    public NotificationPreferenceResponse toNotificationPreferenceResponse(NotificationPreference notificationPreference) {
        return NotificationPreferenceResponse.
                builder().
                id(notificationPreference.getId()).
                userId(notificationPreference.getUserId()).
                isEnabled(notificationPreference.isEnabled()).
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
