package dev.corusoft.eticketia.domain.entities.tickets;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {
    private String description;
    private Double quantity;
    private Double totalAmount;
    private Double unitPrice;
}
