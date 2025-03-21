package web.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import model.NotificationStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private String subject;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
}
