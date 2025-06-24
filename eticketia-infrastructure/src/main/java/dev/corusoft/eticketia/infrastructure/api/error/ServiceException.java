package dev.corusoft.eticketia.infrastructure.api.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a general exception.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends ApiErrorDetails {

  /**
   * Build the exception from the original exception.
   */
  public ServiceException(Exception ex) {
    this(ex.getLocalizedMessage());
  }

  /**
   * Build the exception explaining the cause.
   */
  public ServiceException(String reason) {
    super(reason);
  }

}
