package dev.corusoft.eticketia.domain.entities.tickets;

import com.google.cloud.firestore.annotation.DocumentId;
import dev.corusoft.eticketia.domain.entities.tickets.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    // region Attributes
    @DocumentId
    private Long id;

    private TicketCategory category;
    private TicketSubcategory subcategory;
    private DocumentType documentType;
    private LocalDate purchaseDate;
    private LocalTime purchaseTime;
    private List<LineItem> lineItems;
    private String supplierName;
    private String receiptNumber;
    private BigDecimal totalNet;
    private BigDecimal totalAmount;
    private Locale locale;
    private String country;
    private String currency;
    private List<Tax> taxes;
    private TicketMetadata metadata;

    // endregion Attributes

}