package dev.corusoft.eticketia.domain.entities.tickets;

import com.google.cloud.firestore.annotation.DocumentId;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketCategory;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketDocumentType;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketSubcategory;
import dev.corusoft.eticketia.domain.repositories.Identifiable;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket implements Identifiable {

  // region Attributes
  @EqualsAndHashCode.Include
  @Accessors(chain = false)
  @DocumentId
  private String id;

  private TicketCategory category;
  private TicketSubcategory subcategory;
  private TicketDocumentType documentType;
  private String purchaseDate;
  private String purchaseTime;
  private List<TicketLineItem> lineItems;
  private String supplierName;
  private String receiptNumber;
  private BigDecimal totalNet;
  private BigDecimal totalAmount;
  private String locale;
  private String country;
  private String currency;
  private List<TicketTax> taxes;
  private TicketMetadata metadata;

  // endregion Attributes

}