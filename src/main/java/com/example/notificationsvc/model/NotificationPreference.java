package com.example.notificationsvc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @Column(nullable = false)
    private String email;

    public NotificationPreference(UUID userId, boolean enabled, String email, LocalDateTime createdOn, LocalDateTime updatedOn) {
        this.userId = userId;
        this.enabled = enabled;
        this.email = email;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }


}
