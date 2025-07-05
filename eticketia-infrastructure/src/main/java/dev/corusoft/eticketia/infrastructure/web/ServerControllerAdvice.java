package dev.corusoft.eticketia.infrastructure.web;

import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.domain.exceptions.auth.EmailAlreadyRegisteredException;
import dev.corusoft.eticketia.domain.exceptions.auth.UserNotFoundException;
import dev.corusoft.eticketia.infrastructure.Translator;
import dev.corusoft.eticketia.infrastructure.api.ApiResponse;
import dev.corusoft.eticketia.infrastructure.api.ApiResponseBuilder;
import dev.corusoft.eticketia.infrastructure.api.error.ErrorApiResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller to handle all the customized exceptions created for the application
 */
@Log4j2
@ControllerAdvice(basePackages = {"dev.corusoft.eticketia.server.controllers"})
@RequiredArgsConstructor
public class ServerControllerAdvice {
  private final Translator translator;

  @ExceptionHandler(DomainException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleDomainException(DomainException ex) {
    logError(ex);

    String errorMessage = translator.generateMessage(ex);

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage, ex);
  }

  private void logError(Exception ex) {
    log.error(
        "A {} exception was thrown: {}",
        ex.getClass().getSimpleName(), ex.getMessage(), ex
    );
  }

  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleEmailAlreadyRegisteredException(
      EmailAlreadyRegisteredException ex) {
    logError(ex);

    String errorMessage = translator.generateMessage(ex);

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage, ex);
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody

  public ApiResponse<ErrorApiResponseBody> handleUserNotFoundException(UserNotFoundException ex) {
    logError(ex);

    String errorMessage = translator.generateMessage(ex);

    return ApiResponseBuilder.error(HttpStatus.NOT_FOUND, errorMessage, ex);
  }


}
