package com.example.notificationsvc.service;

import com.example.notificationsvc.model.NotificationPreference;
import com.example.notificationsvc.repository.NotificationPreferenceRepository;
import com.example.notificationsvc.web.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public NotificationScheduler(NotificationService notificationService, NotificationPreferenceRepository notificationPreferenceRepository) {
        this.notificationService = notificationService;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }


    @Scheduled(cron = "0 0 9 1 * *")
    public void sendPromotionalEmails() {
        log.info("Promo emails have been sent");

        List<NotificationPreference> activeUsers = notificationPreferenceRepository.findAllUsersIdByEnabled(true);

        for (NotificationPreference preference : activeUsers) {
            try {
                notificationService.sendNotification(
                        new NotificationRequest(
                                preference.getUserId(),
                                "🔥 Special offers in NutriBoost! 🔥",
                                "Sign in NutriBoost now and grab your offer! 🚀"
                        )
                );
                log.info("Promo emails have been sent");
            } catch (Exception e) {
                log.error("Error while sending promo email", e);
            }
        }
    }
}
