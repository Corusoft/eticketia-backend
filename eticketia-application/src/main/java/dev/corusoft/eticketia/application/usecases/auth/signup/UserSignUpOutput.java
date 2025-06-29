package dev.corusoft.eticketia.application.usecases.auth.signup;

import dev.corusoft.eticketia.application.UseCaseOutput;
import dev.corusoft.eticketia.domain.entities.users.User;

/**
 * User data after executing a sign up.
 */
public record UserSignUpOutput(
    User signedUpUser
) implements UseCaseOutput {
}
