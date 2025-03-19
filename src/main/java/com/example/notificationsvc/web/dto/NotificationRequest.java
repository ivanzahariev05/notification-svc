package com.example.notificationsvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private UUID userId;

    private String subject;

    private String body;

    private LocalDateTime createdAt;

    public NotificationRequest(UUID userId, String subject, String body) {
      this.userId = userId;
      this.subject = subject;
      this.body = body;
    }
}
