package dev.corusoft.eticketia.application.usecases.auth.signup;

import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.MAX_PASSWORD_LENGTH;
import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.MIN_PASSWORD_LENGTH;

import dev.corusoft.eticketia.application.UseCaseInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Parameters to execute a signup with email and password.
 *
 * @param email    User email
 * @param password User password
 */
public record EmailPasswordSignUpInput(
    @NotNull
    @Email
    String email,
    @NotNull
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    String password,
    @NotBlank
    String nickname
) implements UseCaseInput {
}
