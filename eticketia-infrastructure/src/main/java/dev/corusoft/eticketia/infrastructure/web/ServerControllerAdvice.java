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
@ControllerAdvice(basePackages = {"dev.corusoft.eticketia.server.controllers"})
@RequiredArgsConstructor
@Log4j2
public class ServerControllerAdvice {
  private final Translator translator;

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(DomainException.class)
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

  @ResponseBody
  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  public ApiResponse<ErrorApiResponseBody> handleEmailAlreadyRegisteredException(
      EmailAlreadyRegisteredException ex) {
    logError(ex);

    String errorMessage = translator.generateMessage(ex);

    return ApiResponseBuilder.error(HttpStatus.CONFLICT, errorMessage, ex);
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserNotFoundException.class)
  public ApiResponse<ErrorApiResponseBody> handleUserNotFoundException(UserNotFoundException ex) {
    logError(ex);

    String errorMessage = translator.generateMessage(ex);

    return ApiResponseBuilder.error(HttpStatus.NOT_FOUND, errorMessage, ex);
  }


}
