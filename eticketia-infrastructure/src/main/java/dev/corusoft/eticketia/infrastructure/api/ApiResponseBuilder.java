package dev.corusoft.eticketia.infrastructure.api;

import dev.corusoft.eticketia.infrastructure.api.error.ApiErrorDetails;
import dev.corusoft.eticketia.infrastructure.api.error.ErrorApiResponseBody;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponseBuilder {

  // region Success responses

  /**
   * Builds a successful response with empty body
   */
  public static ApiResponse<Void> buildEmptySuccessApiResponse() {
    return createResponse(true, null);
  }

  // region Auxiliar methods
  public static <T> ApiResponse<T> createResponse(boolean success, T body) {
    return ApiResponse.<T>builder()
        .success(success)
        .data(body)
        .timestamp(LocalDateTime.now())
        .build();
  }

  // endregion Success responses

  // region Error responses

  /**
   * Builds a successful response with the given content in the body
   *
   * @param content response content
   * @param <T>     Type of the response content
   */
  public static <T> ApiResponse<T> buildSuccessApiResponse(T content) {
    return createResponse(true, content);
  }

  /**
   * Builds an error response with the given HTTP status and message
   *
   * @param status  HTTP Status that identifies the error
   * @param message Cause of the error
   */
  public static ApiResponse<ErrorApiResponseBody> buildErrorApiResponse(HttpStatus status,
                                                                        String message) {
    return buildErrorApiResponse(status, message, null);
  }

  /**
   * Builds an error response with the given HTTP status, a message and the exception cause
   *
   * @param status    HTTP Status that identifies the error
   * @param message   Cause of the error
   * @param exception Exception that caused the error
   */
  public static ApiResponse<ErrorApiResponseBody> buildErrorApiResponse(HttpStatus status,
                                                                        String message,
                                                                        Exception exception) {
    ErrorApiResponseBody body = generateErrorResponseBody(status, message, exception);

    return createResponse(false, body);
  }
  // endregion Error responses

  private static <E extends Exception> ErrorApiResponseBody generateErrorResponseBody(
      HttpStatus status, String message, E exception) {
    return generateErrorResponseBody(status, message, exception, null);
  }

  private static <E extends Exception> ErrorApiResponseBody generateErrorResponseBody(
      HttpStatus status, String message, E exception,
      List<? extends ApiErrorDetails> errorDetailsList) {
    String debugMessage = (exception != null) ? exception.getLocalizedMessage() : null;
    boolean hasErrors = (errorDetailsList != null) && (!errorDetailsList.isEmpty());
    List<? extends ApiErrorDetails> errorDetails = hasErrors ? errorDetailsList : null;

    return ErrorApiResponseBody.builder()
        .status(status.name())
        .statusCode(status.value())
        .message(message)
        .debugMessage(debugMessage)
        .errors(errorDetails)
        .build();
  }

  /**
   * Builds an error response with the given HTTP status, a message, the exception cause and a list
   * of the error details
   *
   * @param status       HTTP Status that identifies the error
   * @param message      Cause of the error
   * @param exception    Exception that caused the error
   * @param errorDetails List of details that explain the cause of the error response
   */
  public static ApiResponse<ErrorApiResponseBody> buildErrorApiResponse(HttpStatus status,
                                                                        String message,
                                                                        Exception exception,
                                                                        List<? extends ApiErrorDetails> errorDetails) {
    ErrorApiResponseBody body = generateErrorResponseBody(status, message, exception, errorDetails);

    return createResponse(false, body);
  }

  // endregion Auxiliar methods
}
