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
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
@Order(Ordered.LOWEST_PRECEDENCE)
@Log4j2
@RequiredArgsConstructor
public class CommonControllerAdvice {

  private final Translator translator;

  /**
   * Default exception handler to manage uncaught exceptions on runtime.
   *
   * @param ex Exception that is not managed by another {@link ExceptionHandler}
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({RuntimeException.class})
  public ApiResponse<ErrorApiResponseBody> handleUnexpectedExceptions(Exception ex) {
    log.error("An uncaught exception was thrown on runtime: {}", ex.getMessage(), ex);

    String errorMessage = translator.generateMessage(Exception.class.getName());

    return ApiResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, ex);
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<ErrorApiResponseBody> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    logError(ex);

    List<ApiValidationErrorDetails> errorsList = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(ApiValidationErrorDetails::new)
        .toList();
    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return error(HttpStatus.BAD_REQUEST, errorMessage, ex, errorsList);
  }


  // region Spring exception handlers

  private void logError(Exception ex) {
    log.error(
        "A {} exception was thrown: {}",
        ex.getClass().getSimpleName(), ex.getMessage(), ex
    );
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ApiResponse<ErrorApiResponseBody> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex) {
    logError(ex);

    Object[] args = {ex.getParameterName(), ex.getParameterType()};
    String errorMessage = translator.generateMessage(ex.getClass().getName(), args);

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ApiResponse<ErrorApiResponseBody> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    logError(ex);

    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage);
  }

  // endregion Spring exception handlers

  // region Firebase exception handlers

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(FirebaseAuthException.class)
  public ApiResponse<ErrorApiResponseBody> handleFirebaseAuthException(FirebaseAuthException ex) {
    logError(ex);

    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, errorMessage, ex);
  }

  // endregion Firebase exception handlers

}
