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
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Mapper that associates exceptions thrown by Firebase to the domain exceptions.
 */
@Log4j2
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
        .map(doHandleException(exception, args))
        .orElseThrow(
            () -> {
              String message = "No handler registered for Firebase exception " + errorCode;
              log.error(message);

              throw new RuntimeException(message, exception);
            }
        );
  }

  private static Function<FirebaseAuthExceptionHandler, DomainException> doHandleException(
      FirebaseAuthException exception, Object args) {
    log.debug("Handling exception of type '{}'", exception.getAuthErrorCode());
    if (args == null) {
      return handler -> handler.handleException(exception);
    }

    return handler -> handler.handleException(exception, args);
  }

}
