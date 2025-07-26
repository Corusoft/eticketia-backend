package dev.corusoft.eticketia.infrastructure.dtos.auth.inbound;

import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.MAX_PASSWORD_LENGTH;
import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.MIN_PASSWORD_LENGTH;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Parameters to sign up a new user using Firebase implementation.
 */
public record FirebaseEmailPasswordSignUpInputDTO(
    @NotNull @Email String email,
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH) String password,
    @NotBlank String nickname
) {

}
