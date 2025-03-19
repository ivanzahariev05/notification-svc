package com.example.notificationsvc.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String body;

    private boolean isDeleted;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

}
