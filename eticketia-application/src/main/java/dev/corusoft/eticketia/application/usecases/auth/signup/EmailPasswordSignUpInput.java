package dev.corusoft.eticketia.application.usecases.auth.signup;

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
    @Size(min = 6)
    String password,
    @NotBlank
    String nickname
) implements UseCaseInput {
}
