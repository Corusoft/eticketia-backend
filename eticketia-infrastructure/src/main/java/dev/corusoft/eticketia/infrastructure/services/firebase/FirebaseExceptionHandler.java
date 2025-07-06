package dev.corusoft.eticketia.infrastructure.services.firebase;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.infrastructure.api.error.ServiceException;
import dev.corusoft.eticketia.infrastructure.services.firebase.handlers.FirebaseAuthExceptionHandler;
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
@Component
@RequiredArgsConstructor
@Log4j2
public class FirebaseExceptionHandler {
  private final List<FirebaseAuthExceptionHandler> exceptionHandlers;
  private final Map<AuthErrorCode, FirebaseAuthExceptionHandler> cachedHandlers =
      new EnumMap<>(AuthErrorCode.class);


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
        .ofNullable(getExceptionHandlerForError(errorCode))
        .map(doHandleException(exception, args))
        .orElseThrow(
            () -> {
              String message = "No handler registered for Firebase exception " + errorCode;
              log.error(message);

              throw new ServiceException(message, exception);
            }
        );
  }

  public FirebaseAuthExceptionHandler getExceptionHandlerForError(AuthErrorCode errorCode) {
    Function<AuthErrorCode, FirebaseAuthExceptionHandler> doCacheHandler =
        authErrorCode -> exceptionHandlers.stream()
            .filter(handler -> handler.getHandledError().equals(authErrorCode))
            .findFirst()
            .orElseThrow(
                () -> {
                  String message = "No handler registered for Firebase exception " + authErrorCode;
                  log.error(message);

                  throw new ServiceException(message);
                }
            );

    // Try to get the handler, or create it and cache it
    return cachedHandlers.computeIfAbsent(errorCode, doCacheHandler);
  }

  private static Function<FirebaseAuthExceptionHandler, DomainException> doHandleException(
      FirebaseAuthException exception, Object args) {
    if (args == null) {
      log.debug("Handling exception of type '{}' with no args", exception.getAuthErrorCode());
      return handler -> handler.handleException(exception);
    }

    log.debug("Handling exception of type '{}' with args", exception.getAuthErrorCode());
    return handler -> handler.handleException(exception, args);
  }

}
