package dev.corusoft.eticketia.infrastructure.web;

import static dev.corusoft.eticketia.infrastructure.api.ApiResponseBuilder.buildErrorApiResponse;

import dev.corusoft.eticketia.infrastructure.Translator;
import dev.corusoft.eticketia.infrastructure.api.ApiResponse;
import dev.corusoft.eticketia.infrastructure.api.error.ApiValidationErrorDetails;
import dev.corusoft.eticketia.infrastructure.api.error.ErrorApiResponseBody;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class CommonControllerAdvice {
  private final Translator translator;

  // region Spring exception handlers
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<ErrorApiResponseBody> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception, Locale locale) {
    List<ApiValidationErrorDetails> errorsList = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(ApiValidationErrorDetails::new)
        .toList();
    String errorMessage = translator.generateMessage(exception.getClass().getName(), locale);

    return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage, exception, errorsList);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception, Locale locale) {
    Object[] args = {exception.getParameterName(), exception.getParameterType()};
    String errorMessage = translator.generateMessage(exception.getClass().getName(), args, locale);

    return buildErrorApiResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  // endregion Spring exception handlers

}
