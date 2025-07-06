package dev.corusoft.eticketia.infrastructure.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a general exception.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceException extends RuntimeException {

  /**
   * Build the exception from the original exception.
   */
  public ServiceException(Exception ex) {
    super(ex);
  }

  /**
   * Build the exception explaining the cause.
   */
  public ServiceException(String reason) {
    super(reason);
  }

  /**
   * Build the exception explaining the cause and the original exception.
   */
  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
