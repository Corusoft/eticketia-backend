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
import dev.corusoft.eticketia.domain.exceptions.PersistenceErrorException;
import dev.corusoft.eticketia.domain.exceptions.ticket.UnableToParseTicketException;
import dev.corusoft.eticketia.domain.repositories.tickets.TicketRepository;
import dev.corusoft.eticketia.infrastructure.services.mindee.MindeeAdapter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
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
public class MindeeScanTicketUseCaseImpl implements ScanTicketUseCase {

  private final MindeeClient mindee;
  private final TicketRepository ticketRepository;

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
    Ticket parsedTicket = parseTicketContent(input);
    saveTicket(parsedTicket);

    return buildOutput(parsedTicket);
  }


  @Override
  public ScanTicketOutput buildOutput(Object result) {
    Ticket ticket = (Ticket) result;
    return new ScanTicketOutput(ticket);
  }

  private Ticket parseTicketContent(ScanTicketInput input) throws UnableToParseTicketException {
    String fileName = "temp_%s_%s".formatted(input.userId(), input.scanTimestamp().getTime());
    LocalInputSource imageSource = new LocalInputSource(input.imageFile(), fileName);

    Ticket parsedTicket;
    log.debug("Parsing ticket content...");
    try {
      PredictResponse<ReceiptV5> prediction = mindee.parse(ReceiptV5.class, imageSource);
      ReceiptV5Document receiptDocument = prediction.getDocument()
          .getInference()
          .getPrediction();
      parsedTicket = buildTicketFromDocument(receiptDocument);
    } catch (IOException e) {
      log.error("Error parsing ticket content", e);
      throw new UnableToParseTicketException();
    }
    log.debug("Ticket parsed successfully");

    return parsedTicket;
  }

  private void saveTicket(Ticket ticket) throws PersistenceErrorException {
    log.debug("Saving ticket...");
    try {
      ticketRepository.save(ticket);
    } catch (PersistenceErrorException e) {
      log.error("Error saving ticket in Firebase", e);
      throw e;
    }
    log.debug("Ticket saved successfully");
  }

  private static Ticket buildTicketFromDocument(ReceiptV5Document doc) {
    TicketCategory category = MindeeAdapter.toTicketCategory(doc.getCategory());
    TicketSubcategory subcategory = MindeeAdapter.toTicketSubcategory(doc.getSubcategory());
    TicketDocumentType documentType = MindeeAdapter.toTicketDocumentType(doc.getDocumentType());
    String date = MindeeAdapter.toDate(doc.getDate());
    String time = MindeeAdapter.toTime(doc.getTime()).toString();
    List<TicketLineItem> lineItems = MindeeAdapter.toLineItemsList(doc.getLineItems());
    String supplierName = doc.getSupplierName().getValue();
    String receiptNumber = doc.getReceiptNumber().getValue();
    List<TicketTax> taxes = MindeeAdapter.toTicketTaxList(doc.getTaxes());
    BigDecimal totalNet = MindeeAdapter.toBigDecimal(doc.getTotalNet());
    BigDecimal totalAmount = MindeeAdapter.toBigDecimal(doc.getTotalAmount());
    String locale = MindeeAdapter.toLocale(doc.getLocale()).toLanguageTag();
    String currency = MindeeAdapter.toCurrency(doc).toString();

    return Ticket.builder()
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
        .country(doc.getLocale().getCountry())
        .currency(currency)
        .taxes(taxes)
        .metadata(null)
        .build();
  }
}
