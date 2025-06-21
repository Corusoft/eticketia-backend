package dev.corusoft.eticketia.infrastructure.api.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class ApiErrorDetails extends Exception {
  private String reason;

  protected ApiErrorDetails(String reason) {
    super(reason);
    this.reason = reason;
  }
}
