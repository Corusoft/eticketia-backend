package dev.corusoft.eticketia.domain.entities.tickets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTax {

  private Double value;
  private String code;
  private Double rate;
  private Double base;

}
