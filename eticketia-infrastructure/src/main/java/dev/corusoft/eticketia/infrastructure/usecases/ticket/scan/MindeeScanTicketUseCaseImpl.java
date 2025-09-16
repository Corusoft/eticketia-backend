package dev.corusoft.eticketia.infrastructure.usecases.ticket.scan;

import com.google.firebase.cloud.StorageClient;
import com.mindee.MindeeClient;
import com.mindee.input.LocalInputSource;
import com.mindee.parsing.common.AsyncPredictResponse;
import com.mindee.parsing.common.PredictResponse;
import com.mindee.product.receipt.ReceiptV5;
import com.mindee.product.receipt.ReceiptV5Document;
import dev.corusoft.eticketia.application.usecases.ticket.scan.ScanTicketInput;
import dev.corusoft.eticketia.application.usecases.ticket.scan.ScanTicketOutput;
import dev.corusoft.eticketia.application.usecases.ticket.scan.ScanTicketUseCase;
import dev.corusoft.eticketia.domain.entities.tickets.Ticket;
import dev.corusoft.eticketia.domain.entities.tickets.TicketMetadata;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.domain.exceptions.PersistenceErrorException;
import dev.corusoft.eticketia.domain.exceptions.ticket.UnableToParseTicketException;
import dev.corusoft.eticketia.domain.repositories.tickets.TicketRepository;
import dev.corusoft.eticketia.infrastructure.services.mindee.MindeeAdapter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ScanTicketUseCase} using Mindee}.
 */
@Service
@Lazy
@Log4j2
@RequiredArgsConstructor
public class MindeeScanTicketUseCaseImpl implements ScanTicketUseCase {

  private final MindeeClient mindee;
  private final TicketRepository ticketRepository;
  private final StorageClient storageClient;

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
    // Create async tasks
    CompletableFuture<ReceiptV5Document> parseTicketFuture = parseTicketContentAsync(input);

    var finalTicketResult = CompletableFuture
        // Await tasks to finish
        .allOf(parseTicketFuture)
        .thenApplyAsync(nothing -> {
          ReceiptV5Document parsedTicket = parseTicketFuture.join();
          String imageUrl = persistTicketFuture.join();

          return MindeeAdapter.toTicket(parsedTicket);
        })
        // Store ticket entity in database
        .thenCompose(this::persistTicket);

    return buildOutput(parsedTicket);
  }


  @Override
  public ScanTicketOutput buildOutput(Object result) {
    Ticket ticket = (Ticket) result;
    return new ScanTicketOutput(ticket);
  }

  @Async("taskExecutor")
  private CompletableFuture<ReceiptV5Document> parseTicketContentAsync(ScanTicketInput input) {
    LocalInputSource imageSource = new LocalInputSource(input.imageFile(), input.fileName());

    return CompletableFuture.supplyAsync(parseTicketContentTask(imageSource));
  }

  private void persistTicket(Ticket ticket) throws PersistenceErrorException {
    log.debug("Saving ticket...");
    try {
      ticketRepository.save(ticket);
      // TODO Guardar ticket en un bucket en Firebase, recuperar datos y guardar en metadatos

    } catch (PersistenceErrorException e) {
      log.error("Error saving ticket in Firebase", e);
      throw e;
    }
    log.debug("Ticket saved successfully");
  }

  private Supplier<ReceiptV5Document> parseTicketContentTask(LocalInputSource imageSource) {
    return () -> {
      log.debug("Parsing ticket content asynchronously...");

      ReceiptV5Document receiptDocument;
      try {
        AsyncPredictResponse<ReceiptV5> prediction = mindee.enqueueAndParse(ReceiptV5.class, imageSource);
        receiptDocument = prediction.getDocumentObj().getInference().getPrediction();
      } catch (IOException | InterruptedException e) {
        log.error("Error parsing ticket content asynchronously", e);
        throw new CompletionException(new UnableToParseTicketException());
      }

      log.debug("Ticket parsed successfully");
      return receiptDocument;
    };
  }

  @Async("taskExecutor")
  private CompletableFuture<String> uploadFileToStorageAsync(ScanTicketInput input) {
    String bucketName = storageClient.bucket().getName();
    String ticketFileName = "tickets/%s/%d_%s".formatted(
        input.userId()
    )
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
      parsedTicket = MindeeAdapter.toTicket(receiptDocument);
      // Set OCR response in ticket metadata
      TicketMetadata metadata = TicketMetadata.builder()
          .rawOcrResponse(receiptDocument.toString())
          .build();
      parsedTicket.setMetadata(metadata);
    } catch (IOException e) {
      log.error("Error parsing ticket content", e);
      throw new UnableToParseTicketException();
    }
    log.debug("Ticket parsed successfully");

    return parsedTicket;
  }

  private TicketMetadata generateTicketMetadata(ScanTicketInput input, ReceiptV5Document document) {
    return TicketMetadata.builder()
        .scanTimestamp(input.scanTimestamp())
        .build();
  }

}
