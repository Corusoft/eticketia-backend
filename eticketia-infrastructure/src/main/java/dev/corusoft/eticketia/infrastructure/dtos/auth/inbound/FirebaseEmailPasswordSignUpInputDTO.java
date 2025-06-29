package dev.corusoft.eticketia.infrastructure.dtos.auth.inbound;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record FirebaseEmailPasswordSignUpInputDTO(
    @NotNull
    @Email
    String email,
    @Size(min = 6)
    String password,
    @NotBlank
    String nickname
) {
}
