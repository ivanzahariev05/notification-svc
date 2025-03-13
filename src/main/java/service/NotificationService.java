package service;

import lombok.extern.slf4j.Slf4j;
import model.Notification;
import model.NotificationPreference;
import model.NotificationStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import repository.NotificationPreferenceRepository;
import repository.NotificationRepository;
import web.dto.NotificationRequest;
import web.dto.UpsertNotificationPreference;
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

        Optional<NotificationPreference> userPreferenceOptional = notificationPreferenceRepository.findUserById(upsertNotificationPreference.getUserId());
        if (userPreferenceOptional.isPresent()) {
            NotificationPreference userPreference = userPreferenceOptional.get();
            userPreference.setEnabled(upsertNotificationPreference.isNotificationEnabled());
            userPreference.setUpdatedOn(LocalDateTime.now());
            return notificationPreferenceRepository.save(userPreference);
        }

        NotificationPreference notificationPreference = NotificationPreference.builder()
                .userId(upsertNotificationPreference.getUserId())
                .enabled(upsertNotificationPreference.isNotificationEnabled())
                .updatedOn(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();
         return notificationPreferenceRepository.save(notificationPreference);
    }

    public NotificationPreference getPreferenceByUserId(UUID userId) {
        Optional<NotificationPreference> userById = notificationPreferenceRepository.findUserById(userId);
        return userById.orElseThrow(() -> new NullPointerException("User with id " + userId + " not found"));
    }

    public Notification sendNotification(NotificationRequest notificationRequest) {

        UUID userId = notificationRequest.getUserId();
        NotificationPreference notificationPreference = getPreferenceByUserId(userId);

        if (!notificationPreference.isEnabled()){
            throw new IllegalArgumentException("Notification preference is disabled for user with id: " + userId);
        }

       SimpleMailMessage message = new SimpleMailMessage();
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
