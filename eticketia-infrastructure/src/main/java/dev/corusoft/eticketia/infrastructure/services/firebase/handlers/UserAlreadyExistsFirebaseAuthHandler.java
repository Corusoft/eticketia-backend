package dev.corusoft.eticketia.infrastructure.services.firebase.handlers;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.domain.exceptions.auth.UserAlreadyExistsException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class UserAlreadyExistsFirebaseAuthHandler implements FirebaseAuthExceptionHandler {
  @Override
  public DomainException handleException(FirebaseAuthException exception, Object context) {
    if (context instanceof String uid) {
      return new UserAlreadyExistsException(uid, exception);
    }

    return new UserAlreadyExistsException("<<unknown uid>>", exception);
  }

  @Override
  public AuthErrorCode getHandledError() {
    return AuthErrorCode.UID_ALREADY_EXISTS;
  }
}
