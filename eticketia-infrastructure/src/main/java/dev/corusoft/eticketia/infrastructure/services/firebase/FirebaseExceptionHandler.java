package dev.corusoft.eticketia.infrastructure.services.firebase;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.infrastructure.services.firebase.handlers.FirebaseAuthExceptionHandler;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper that associates exceptions thrown by Firebase to the domain exceptions.
 */
@RequiredArgsConstructor
@Component
public class FirebaseExceptionHandler {
  private final List<FirebaseAuthExceptionHandler> exceptionHandlers;
  private final Map<AuthErrorCode, FirebaseAuthExceptionHandler> cachedHandlers =
      new EnumMap<>(AuthErrorCode.class);

  /**
   * Cache handlers for fast lookups
   */
  @PostConstruct
  public void cacheExceptionHandlers() {
    exceptionHandlers
        .forEach(handler -> cachedHandlers.put(handler.getHandledError(), handler));
  }

  /**
   * Associates the exception with the corresponding domain exception.
   *
   * @param exception Exception thrown by {@link FirebaseAuth}
   * @return The corresponding domain exception
   */
  public DomainException toDomainException(FirebaseAuthException exception) {
    return toDomainException(exception, null);
  }

  /**
   * Associates the exception with the corresponding domain exception.
   * If the {@link DomainException} requires an argument, it will be extracted from {@code args}
   *
   * @param exception Exception thrown by {@link FirebaseAuth}
   * @param args      Context from which the {@link DomainException} extracts the needed values
   * @return The corresponding domain exception
   */
  public DomainException toDomainException(FirebaseAuthException exception, Object args) {
    AuthErrorCode errorCode = exception.getAuthErrorCode();

    return Optional
        .ofNullable(cachedHandlers.get(errorCode))
        .map(handler -> handler.handleException(exception, args))
        .orElseThrow(
            () -> new RuntimeException("No handler registered for Firebase exception", exception)
        );
  }

}
