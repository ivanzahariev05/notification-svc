package com.example.notificationsvc;


import com.example.notificationsvc.model.Notification;
import com.example.notificationsvc.model.NotificationPreference;
import com.example.notificationsvc.model.NotificationStatus;
import com.example.notificationsvc.web.dto.NotificationPreferenceResponse;
import com.example.notificationsvc.web.dto.NotificationResponse;
import com.example.notificationsvc.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoMapperTest {

    @Test
    void testToNotificationPreferenceResponse() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        LocalDateTime now = LocalDateTime.now();

        NotificationPreference preference = new NotificationPreference(userId, true, email, now, now);

        NotificationPreferenceResponse response = DtoMapper.toNotificationPreferenceResponse(preference);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(true, response.isEnabled());
    }

    @Test
    void testToNotificationRequest() {
        LocalDateTime now = LocalDateTime.now();
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Notification notification = new Notification(
                notificationId, userId, "Test Subject", "Test Body", false, now, NotificationStatus.SUCCEED
        );


        NotificationResponse response = DtoMapper.toNotificationRequest(notification);


        assertNotNull(response);
        assertEquals("Test Subject", response.getSubject());
        assertEquals(now, response.getCreatedAt());
        assertEquals(NotificationStatus.SUCCEED, response.getStatus());
    }
}
