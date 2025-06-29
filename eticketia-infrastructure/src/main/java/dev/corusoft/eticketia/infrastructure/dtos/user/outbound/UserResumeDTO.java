package dev.corusoft.eticketia.infrastructure.dtos.user.outbound;

import java.time.LocalDateTime;

/**
 * Resumed version of User details
 */
public record UserResumeDTO(
    String uid,
    String email,
    String displayName,
    LocalDateTime registrationDate
) {
}
