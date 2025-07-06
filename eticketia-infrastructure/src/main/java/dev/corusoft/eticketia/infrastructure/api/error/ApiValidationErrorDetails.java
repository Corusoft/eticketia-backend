package dev.corusoft.eticketia.infrastructure.api.error;

import org.springframework.validation.FieldError;

public record ApiValidationErrorDetails(
    String objectName,
    String fieldName,
    Object rejectedValue,
    String message
) implements ApiErrorDetails {
  public ApiValidationErrorDetails(FieldError fieldError) {
    this(
        fieldError.getObjectName(),
        fieldError.getField(),
        fieldError.getRejectedValue(),
        fieldError.getDefaultMessage()
    );
  }
}
