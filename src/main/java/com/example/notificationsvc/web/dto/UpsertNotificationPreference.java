package com.example.notificationsvc.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpsertNotificationPreference {

    @NotNull
    private UUID userId;

    private boolean enabled;

    @NotNull
    private String email;


}
