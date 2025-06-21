package dev.corusoft.eticketia.infrastructure.api.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends ApiErrorDetails {

  public ServiceException(String reason) {
    super(reason);
  }

}
