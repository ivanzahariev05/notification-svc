package com.example.notificationsvc.web;

import com.example.notificationsvc.model.NotificationPreference;
import com.example.notificationsvc.service.NotificationService;
import com.example.notificationsvc.web.dto.NotificationPreferenceResponse;
import com.example.notificationsvc.web.dto.UpsertNotificationPreference;
import com.example.notificationsvc.web.mapper.DtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;


    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> upsertNotificationPreference(@RequestBody UpsertNotificationPreference upsertNotificationPreference) {
        NotificationPreference notificationPreference = notificationService.upsertPreference(upsertNotificationPreference);
        NotificationPreferenceResponse notificationPreferenceResponse = DtoMapper.toNotificationPreferenceResponse(notificationPreference);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationPreferenceResponse);
    }

    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> getNotificationPreference(@RequestParam("userId") UUID userId) {
        NotificationPreference preferenceByUserId = notificationService.getPreferenceByUserId(userId);
        NotificationPreferenceResponse notificationPreferenceResponse = DtoMapper.toNotificationPreferenceResponse(preferenceByUserId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationPreferenceResponse);
    }


    @PostMapping("/welcome")
    public ResponseEntity<Void> sendWelcomeEmail(@RequestParam UUID userId) {
        notificationService.sendWelcomeEmail(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/toggle")
    public ResponseEntity<Void> togglePreference(@RequestParam UUID userId) {
        notificationService.togglePreference(userId);
        return ResponseEntity.ok().build();
    }
}
