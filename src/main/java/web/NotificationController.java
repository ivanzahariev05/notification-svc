package web;

import model.Notification;
import model.NotificationPreference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.NotificationService;
import web.dto.NotificationPreferenceResponse;
import web.dto.NotificationRequest;
import web.dto.NotificationResponse;
import web.dto.UpsertNotificationPreference;
import web.mapper.DtoMapper;

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

    @PostMapping()
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        Notification notification = notificationService.sendNotification(notificationRequest);
        NotificationResponse notificationResponse = DtoMapper.toNotificationRequest(notification);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationResponse);
    }
}
