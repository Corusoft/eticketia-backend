package dev.corusoft.eticketia.server;

import static dev.corusoft.eticketia.infrastructure.api.ApiResponseBuilder.error;

import com.google.firebase.auth.FirebaseAuthException;
import dev.corusoft.eticketia.infrastructure.Translator;
import dev.corusoft.eticketia.infrastructure.api.ApiResponse;
import dev.corusoft.eticketia.infrastructure.api.ApiResponseBuilder;
import dev.corusoft.eticketia.infrastructure.api.error.ApiValidationErrorDetails;
import dev.corusoft.eticketia.infrastructure.api.error.ErrorApiResponseBody;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller to handle exceptions thrown by the JVM or Spring framework
 */
@ControllerAdvice
@RequiredArgsConstructor
public class CommonControllerAdvice {

  private final Translator translator;


  // region Java exception handlers

  @ExceptionHandler({RuntimeException.class, NullPointerException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleRuntimeExceptions(Exception exception) {
    String errorMessage = translator.generateMessage(exception.getClass().getName());

    return ApiResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, exception);
  }

  // endregion Java exception handlers


  // region Spring exception handlers

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    List<ApiValidationErrorDetails> errorsList = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(ApiValidationErrorDetails::new)
        .toList();
    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return error(HttpStatus.BAD_REQUEST, errorMessage, ex, errorsList);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception) {
    Object[] args = {exception.getParameterName(), exception.getParameterType()};
    String errorMessage =
        translator.generateMessage(exception.getClass().getName(), args);

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException exception) {
    String errorMessage = translator.generateMessage(exception.getClass().getName());

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage);
  }

  // endregion Spring exception handlers

  // region Firebase exception handlers

  @ExceptionHandler(FirebaseAuthException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<ErrorApiResponseBody> handleFirebaseAuthException(FirebaseAuthException e) {
    String errorMessage = translator.generateMessage(e.getClass().getName());

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage, e);
  }

  // endregion Firebase exception handlers

}
