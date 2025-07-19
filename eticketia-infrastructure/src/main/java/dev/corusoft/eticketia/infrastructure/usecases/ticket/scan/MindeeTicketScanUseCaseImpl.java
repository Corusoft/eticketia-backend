package dev.corusoft.eticketia.infrastructure.usecases.ticket.scan;

import com.mindee.MindeeClient;
import com.mindee.input.LocalInputSource;
import com.mindee.parsing.common.PredictResponse;
import com.mindee.product.receipt.ReceiptV5;
import com.mindee.product.receipt.ReceiptV5Document;
import dev.corusoft.eticketia.application.usecases.ticket.scan.ScanTicketInput;
import dev.corusoft.eticketia.application.usecases.ticket.scan.ScanTicketOutput;
import dev.corusoft.eticketia.application.usecases.ticket.scan.ScanTicketUseCase;
import dev.corusoft.eticketia.domain.entities.tickets.Ticket;
import dev.corusoft.eticketia.domain.entities.tickets.TicketLineItem;
import dev.corusoft.eticketia.domain.entities.tickets.TicketTax;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketCategory;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketDocumentType;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketSubcategory;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.domain.exceptions.ticket.UnableToParseTicketException;
import dev.corusoft.eticketia.infrastructure.services.mindee.MindeeAdapter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ScanTicketUseCase} using Mindee}
 */
@Service
@Lazy
@Log4j2
@RequiredArgsConstructor
public class MindeeTicketScanUseCaseImpl implements ScanTicketUseCase {

  private final MindeeClient mindee;

  @Override
  public String getUseCaseName() {
    return getClass().getSimpleName();
  }

  /**
   * Scans the content of the received ticket image.
   *
   * @param input Arguments to execute the use case.
   * @return Data extracted from the ticket
   * @throws DomainException An error occurred scanning the ticket content
   */
  @Override
  public ScanTicketOutput execute(ScanTicketInput input) throws DomainException {
    String fileName = "temp_%s_%s".formatted(input.userId(), input.scanTimestamp().getTime());
    LocalInputSource imageSource = new LocalInputSource(input.imageFile(), fileName);

    ReceiptV5Document receiptDocument;
    try {
      PredictResponse<ReceiptV5> predictResponse = mindee.parse(ReceiptV5.class, imageSource);
      receiptDocument = predictResponse.getDocument()
          .getInference()
          .getPrediction();

      // TODO guardar ticket en firestore

    } catch (IOException e) {
      log.error("Error scanning ticket content", e);
      throw new UnableToParseTicketException();
    }

    return buildOutput(receiptDocument);
  }

  @Override
  public ScanTicketOutput buildOutput(Object result) {
    ReceiptV5Document document = (ReceiptV5Document) result;
    TicketCategory category = MindeeAdapter.toTicketCategory(document.getCategory());
    TicketSubcategory subcategory = MindeeAdapter.toTicketSubcategory(document.getSubcategory());
    TicketDocumentType documentType = MindeeAdapter.toTicketDocumentType(
        document.getDocumentType());
    LocalDate date = document.getDate().getValue();
    LocalTime time = LocalTime.parse(document.getTime().getValue(), DateTimeFormatter.ISO_TIME);
    List<TicketLineItem> lineItems = MindeeAdapter.toLineItemsList(document.getLineItems());
    String supplierName = document.getSupplierName().getValue();
    String receiptNumber = document.getReceiptNumber().getValue();
    List<TicketTax> taxes = MindeeAdapter.toTicketTaxList(document.getTaxes());
    BigDecimal totalNet = BigDecimal.valueOf(document.getTotalNet().getValue());
    BigDecimal totalAmount = BigDecimal.valueOf(document.getTotalAmount().getValue());
    Locale locale = Locale.of(document.getLocale().getValue());
    Currency currency = Currency.getInstance(locale);

    Ticket ticket = Ticket.builder()
        .category(category)
        .subcategory(subcategory)
        .documentType(documentType)
        .purchaseDate(date)
        .purchaseTime(time)
        .lineItems(lineItems)
        .supplierName(supplierName)
        .receiptNumber(receiptNumber)
        .totalNet(totalNet)
        .totalAmount(totalAmount)
        .locale(locale)
        .country(document.getLocale().getCountry())
        .currency(currency)
        .taxes(taxes)
        .metadata(null)
        .build();

    return new ScanTicketOutput(ticket);
  }
}
