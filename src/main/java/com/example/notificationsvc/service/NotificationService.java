package com.example.notificationsvc.service;

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
        return userById.orElseThrow(() -> new NullPointerException("User with id " + userId + " not found"));
    }

    public Notification sendNotification(NotificationRequest notificationRequest) {

        UUID userId = notificationRequest.getUserId();
        NotificationPreference notificationPreference = getPreferenceByUserId(userId);

        if (!notificationPreference.isEnabled()){
            throw new IllegalArgumentException("Notification preference is disabled for user with id: " + userId);
        }

       SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationPreference.getEmail());
        message.setSubject(notificationRequest.getSubject());
        message.setText(notificationRequest.getBody());


        Notification notification = Notification.builder()
                .subject(notificationRequest.getSubject())
                .body(notificationRequest.getBody())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        try{
            mailSender.send(message);
            notification.setStatus(NotificationStatus.SUCCEED);
        }catch (Exception e){
            notification.setStatus(NotificationStatus.FAILED);
            log.error("There was an issue sending the notification, due to %s", e);
        }
        return notificationRepository.save(notification);
    }
}
