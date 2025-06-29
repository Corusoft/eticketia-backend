package dev.corusoft.eticketia.infrastructure.web;

import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.domain.exceptions.auth.EmailAlreadyRegisteredException;
import dev.corusoft.eticketia.domain.exceptions.auth.UserNotFoundException;
import dev.corusoft.eticketia.infrastructure.Translator;
import dev.corusoft.eticketia.infrastructure.api.ApiResponse;
import dev.corusoft.eticketia.infrastructure.api.ApiResponseBuilder;
import dev.corusoft.eticketia.infrastructure.api.error.ErrorApiResponseBody;
import lombok.RequiredArgsConstructor;
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
public class ServerControllerAdvice {
  private final Translator translator;

  @ExceptionHandler(DomainException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleDomainException(DomainException e) {
    String errorMessage = translator.generateMessage(e);

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage, e);
  }

  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleEmailAlreadyRegisteredException(
      EmailAlreadyRegisteredException e) {
    String errorMessage = translator.generateMessage(e);

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage, e);
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody

  public ApiResponse<ErrorApiResponseBody> handleUserNotFoundException(UserNotFoundException e) {
    String errorMessage = translator.generateMessage(e);

    return ApiResponseBuilder.error(HttpStatus.NOT_FOUND, errorMessage, e);
  }


}
