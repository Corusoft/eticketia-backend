package dev.corusoft.eticketia.domain.entities.tickets;

import com.google.cloud.firestore.annotation.DocumentId;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketCategory;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketDocumentType;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketSubcategory;
import dev.corusoft.eticketia.domain.repositories.Identifiable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket implements Identifiable<Long> {

  // region Attributes
  @DocumentId
  private Long id;

  private TicketCategory category;
  private TicketSubcategory subcategory;
  private TicketDocumentType documentType;
  private LocalDate purchaseDate;
  private LocalTime purchaseTime;
  private List<TicketLineItem> lineItems;
  private String supplierName;
  private String receiptNumber;
  private BigDecimal totalNet;
  private BigDecimal totalAmount;
  private Locale locale;
  private String country;
  private Currency currency;
  private List<TicketTax> taxes;
  private TicketMetadata metadata;

  // endregion Attributes

}