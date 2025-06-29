package dev.corusoft.eticketia.infrastructure.services.firebase.handlers;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import dev.corusoft.eticketia.application.usecases.auth.signup.EmailPasswordSignUpInput;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.domain.exceptions.auth.EmailAlreadyRegisteredException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class EmailAlreadyExistsFirebaseAuthHandler implements FirebaseAuthExceptionHandler {

  @Override
  public DomainException handleException(FirebaseAuthException exception, Object context) {
    if (context instanceof EmailPasswordSignUpInput input) {
      return new EmailAlreadyRegisteredException(input.email());
    }

    return new EmailAlreadyRegisteredException("<<unknown email>>");
  }

  @Override
  public AuthErrorCode getHandledError() {
    return AuthErrorCode.EMAIL_ALREADY_EXISTS;
  }
}
