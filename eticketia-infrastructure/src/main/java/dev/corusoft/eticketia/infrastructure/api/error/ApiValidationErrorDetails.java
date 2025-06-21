package dev.corusoft.eticketia.infrastructure.api.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.FieldError;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiValidationErrorDetails extends ApiErrorDetails {
  private final String objectName;
  private final String fieldName;
  private final String rejectedValue;

  public ApiValidationErrorDetails(FieldError fieldError) {
    super(fieldError.getDefaultMessage());
    this.objectName = fieldError.getObjectName();
    this.fieldName = fieldError.getField();
    this.rejectedValue = (String) fieldError.getRejectedValue();
  }
}
