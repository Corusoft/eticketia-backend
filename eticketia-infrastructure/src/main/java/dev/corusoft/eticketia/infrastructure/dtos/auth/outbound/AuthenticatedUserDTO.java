package dev.corusoft.eticketia.infrastructure.dtos.auth.outbound;


import com.fasterxml.jackson.annotation.JsonProperty;
import dev.corusoft.eticketia.infrastructure.dtos.user.outbound.UserResumeDTO;
import dev.corusoft.eticketia.infrastructure.security.SecurityConstants;

public record AuthenticatedUserDTO(
    @JsonProperty(value = SecurityConstants.TOKEN_ATTRIBUTE_NAME)
    String token,
    @JsonProperty(value = "user") UserResumeDTO userDTO
) {
}
