package dev.corusoft.eticketia.infrastructure.services.firebase.handlers;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.domain.exceptions.auth.UserNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class UserNotFoundFirebaseAuthHandler implements FirebaseAuthExceptionHandler {
  @Override
  public DomainException handleException(FirebaseAuthException exception, Object context) {
    if (context instanceof String uid) {
      return new UserNotFoundException(uid, exception);
    }

    return new UserNotFoundException("<<unknown uid>>", exception);
  }

  @Override
  public AuthErrorCode getHandledError() {
    return AuthErrorCode.USER_NOT_FOUND;
  }
}
