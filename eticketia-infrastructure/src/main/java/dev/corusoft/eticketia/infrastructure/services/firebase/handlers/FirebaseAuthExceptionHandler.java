package dev.corusoft.eticketia.infrastructure.services.firebase.handlers;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import dev.corusoft.eticketia.domain.exceptions.DomainException;

/**
 * Defines a contract to map a {@link FirebaseAuthException} to a {@link DomainException} attending to the {@link AuthErrorCode}.
 */
public interface FirebaseAuthExceptionHandler {

  /**
   * Map the given {@code exception} to a {@link DomainException} that requires no arguments.
   *
   * @param exception Firebase exception to be mapped
   * @return The corresponding {@link DomainException}
   */
  default DomainException handleException(FirebaseAuthException exception) {
    return handleException(exception, null);
  }

  /**
   * Map the given {@code exception} to a {@link DomainException}.<br>
   * In case the {@link DomainException} requires some context, it will be inyected from {@code context}
   *
   * @param exception Firebase exception to be mapped
   * @param context   Context to create the exception, if needed
   * @return The corresponding {@link DomainException}
   */
  DomainException handleException(FirebaseAuthException exception, Object context);

  /**
   * Specify which Firebase {@link AuthErrorCode} is being handled.
   */
  AuthErrorCode getHandledError();
}
