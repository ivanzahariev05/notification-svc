package com.example.notificationsvc;

import com.example.notificationsvc.model.NotificationPreference;
import com.example.notificationsvc.repository.NotificationPreferenceRepository;
import com.example.notificationsvc.service.NotificationScheduler;
import com.example.notificationsvc.service.NotificationService;
import com.example.notificationsvc.web.dto.NotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NotificationSchedulerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendPromotionalEmails_Success() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        NotificationPreference user1 = new NotificationPreference();
        user1.setUserId(userId1);
        user1.setEnabled(true);

        NotificationPreference user2 = new NotificationPreference();
        user2.setUserId(userId2);
        user2.setEnabled(true);

        List<NotificationPreference> activeUsers = List.of(user1, user2);

        when(notificationPreferenceRepository.findAllUsersIdByEnabled(true)).thenReturn(activeUsers);

        notificationScheduler.sendPromotionalEmails();

        ArgumentCaptor<NotificationRequest> notificationCaptor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(notificationService, times(2)).sendNotification(notificationCaptor.capture());

        List<NotificationRequest> capturedNotifications = notificationCaptor.getAllValues();
        assertEquals(2, capturedNotifications.size());

        assertEquals(userId1, capturedNotifications.get(0).getUserId());
        assertEquals("🔥 Special offers in NutriBoost! 🔥", capturedNotifications.get(0).getSubject());
        assertEquals("Sign in NutriBoost now and grab your offer! 🚀", capturedNotifications.get(0).getBody());

        assertEquals(userId2, capturedNotifications.get(1).getUserId());
        assertEquals("🔥 Special offers in NutriBoost! 🔥", capturedNotifications.get(1).getSubject());
        assertEquals("Sign in NutriBoost now and grab your offer! 🚀", capturedNotifications.get(1).getBody());
    }

    @Test
    void testSendPromotionalEmails_NoActiveUsers() {
        when(notificationPreferenceRepository.findAllUsersIdByEnabled(true)).thenReturn(List.of());

        notificationScheduler.sendPromotionalEmails();

        verify(notificationService, never()).sendNotification(any(NotificationRequest.class));
    }
}
