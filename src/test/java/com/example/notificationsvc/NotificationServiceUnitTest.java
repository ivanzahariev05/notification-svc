package com.example.notificationsvc;

import com.example.notificationsvc.model.Notification;
import com.example.notificationsvc.model.NotificationPreference;
import com.example.notificationsvc.repository.NotificationPreferenceRepository;
import com.example.notificationsvc.repository.NotificationRepository;
import com.example.notificationsvc.service.NotificationService;
import com.example.notificationsvc.web.dto.NotificationRequest;
import com.example.notificationsvc.web.dto.UpsertNotificationPreference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock
    private MailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testExample() {
        assertNotNull(notificationService);
    }

    @Test
    void testUpsertPreference_CreateNew() {
        UUID userId = UUID.randomUUID();
        UpsertNotificationPreference request = new UpsertNotificationPreference(userId, true, "user@example.com");

        when(notificationPreferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(notificationPreferenceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationPreference result = notificationService.upsertPreference(request);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.isEnabled());
        assertEquals("user@example.com", result.getEmail());

        verify(notificationPreferenceRepository, times(1)).save(any());
    }

    @Test
    void testGetPreferenceByUserId_Found() {
        UUID userId = UUID.randomUUID();
        NotificationPreference preference = new NotificationPreference(userId, true, "user@example.com", LocalDateTime.now(), LocalDateTime.now());

        when(notificationPreferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));

        NotificationPreference result = notificationService.getPreferenceByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void testSendNotification_Success() {
        UUID userId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest(userId, "Test Subject", "Test Body");
        NotificationPreference preference = new NotificationPreference(userId, true, "user@example.com", LocalDateTime.now(), LocalDateTime.now());

        when(notificationPreferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));
        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Notification result = notificationService.sendNotification(request);

        assertNotNull(result);
        assertEquals("Test Subject", result.getSubject());
        assertEquals("Test Body", result.getBody());
        assertEquals(userId, result.getUserId());

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void testSendWelcomeEmail() {
        UUID userId = UUID.randomUUID();
        NotificationPreference preference = new NotificationPreference(userId, true, "user@example.com", LocalDateTime.now(), LocalDateTime.now());

        when(notificationPreferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));

        notificationService.sendWelcomeEmail(userId);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(1)).save(any());
    }
}
