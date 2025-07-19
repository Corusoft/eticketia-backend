package dev.corusoft.eticketia.domain.entities.tickets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketLineItem {

  private String description;
  private Double quantity;
  private Double totalAmount;
  private Double unitPrice;
}
