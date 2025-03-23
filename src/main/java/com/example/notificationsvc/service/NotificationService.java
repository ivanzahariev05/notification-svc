package com.example.notificationsvc.service;

import com.example.notificationsvc.NoResourceFoundException;
import lombok.extern.slf4j.Slf4j;
import com.example.notificationsvc.model.Notification;
import com.example.notificationsvc.model.NotificationPreference;
import com.example.notificationsvc.model.NotificationStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import com.example.notificationsvc.repository.NotificationPreferenceRepository;
import com.example.notificationsvc.repository.NotificationRepository;
import com.example.notificationsvc.web.dto.NotificationRequest;
import com.example.notificationsvc.web.dto.UpsertNotificationPreference;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final MailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository, NotificationPreferenceRepository notificationPreferenceRepository, MailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.mailSender = mailSender;
    }


    public NotificationPreference upsertPreference(UpsertNotificationPreference upsertNotificationPreference) {

        Optional<NotificationPreference> userPreferenceOptional = notificationPreferenceRepository.findByUserId(upsertNotificationPreference.getUserId());
        if (userPreferenceOptional.isPresent()) {
            NotificationPreference userPreference = userPreferenceOptional.get();
            userPreference.setEnabled(upsertNotificationPreference.isEnabled());
            userPreference.setUpdatedOn(LocalDateTime.now());
            userPreference.setEmail(upsertNotificationPreference.getEmail());
            return notificationPreferenceRepository.save(userPreference);
        }

        NotificationPreference notificationPreference = NotificationPreference.builder()
                .userId(upsertNotificationPreference.getUserId())
                .enabled(upsertNotificationPreference.isEnabled())
                .email(upsertNotificationPreference.getEmail())
                .updatedOn(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();
        return notificationPreferenceRepository.save(notificationPreference);
    }

    public NotificationPreference getPreferenceByUserId(UUID userId) {
        Optional<NotificationPreference> userById = notificationPreferenceRepository.findByUserId(userId);
        return userById.orElseThrow(() -> new NoResourceFoundException("User with id " + userId + " not found"));
    }

    public Notification sendNotification(NotificationRequest notificationRequest) {
        log.info("Received sendNotification request with DTO: {}", notificationRequest);

        if (notificationRequest == null || notificationRequest.getUserId() == null) {
            log.error("NotificationRequest or userId is null! Something is wrong.");
            throw new IllegalArgumentException("Invalid NotificationRequest: userId is missing");
        }

        return sendNotification(notificationRequest.getUserId(), notificationRequest.getSubject(), notificationRequest.getBody());
    }

    public Notification sendNotification(UUID userId, String subject, String body) {
        log.info("Processing sendNotification for userId: {}, subject: {}", userId, subject);

        NotificationPreference notificationPreference = getPreferenceByUserId(userId);

        if (!notificationPreference.isEnabled()) {
            log.warn("Notification preference is disabled for userId: {}", userId);
            throw new IllegalArgumentException("Notification preference is disabled for user: " + userId);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationPreference.getEmail());
        message.setSubject(subject);
        message.setText(body);

        Notification notification = Notification.builder()
                .userId(userId)
                .subject(subject)
                .body(body)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        NotificationStatus status;
        try {
            mailSender.send(message);
            status = NotificationStatus.SUCCEED;
            log.info("Email successfully sent to {}", notificationPreference.getEmail());
        } catch (Exception e) {
            status = NotificationStatus.FAILED;
            log.error("Failed to send email to {}", notificationPreference.getEmail(), e);
        }

        notification.setStatus(status);
        return notificationRepository.save(notification);
    }

    public void
    sendWelcomeEmail(UUID userId) {
        NotificationPreference preference = getPreferenceByUserId(userId);

        if (!preference.isEnabled()) {
            log.warn("Notification preference is disabled for user with id: " + userId);
            return;
        }

        Notification notification = Notification.builder()
                .userId(userId)
                .subject("Welcome to NutriBoost!")
                .body("Thank you for signing up! We are glad to have you with us.")
                .status(NotificationStatus.SUCCEED)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        notificationRepository.save(notification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(preference.getEmail());
        message.setSubject("Welcome to NutriBoost!");
        message.setText("Thank you for signing up! We are glad to have you with us.");

        try {
            mailSender.send(message);
            log.info("Welcome email sent");
        } catch (Exception e) {
            log.error("Failed to send welcome email");
        }
    }

    public void togglePreference(UUID userId) {
        NotificationPreference preference = notificationPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Preference not found"));

        preference.setEnabled(!preference.isEnabled());
        notificationPreferenceRepository.save(preference);
    }
}
