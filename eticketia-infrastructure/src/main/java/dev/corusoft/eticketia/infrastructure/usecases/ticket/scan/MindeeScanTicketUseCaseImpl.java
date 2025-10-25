package dev.corusoft.eticketia.infrastructure.usecases.ticket.scan;

import com.mindee.InferenceParameters;
import com.mindee.MindeeClientV2;
import com.mindee.input.LocalInputSource;
import com.mindee.parsing.common.AsyncPredictResponse;
import com.mindee.product.receipt.ReceiptV5;
import com.mindee.product.receipt.ReceiptV5Document;
import dev.corusoft.eticketia.application.services.StorageService;
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
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

/**
 * Implementation of {@link ScanTicketUseCase} using Mindee}.
 */
@Service
@Lazy
@Log4j2
@RequiredArgsConstructor
public class MindeeScanTicketUseCaseImpl implements ScanTicketUseCase {

  private final MindeeClientV2 mindee;
  private final TicketRepository ticketRepository;
  private final StorageService storageService;

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
    Ticket ticket = parseTicket(input);

    return buildOutput(ticket);
  }

  @Override
  public ScanTicketOutput buildOutput(Object result) {
    Ticket ticket = (Ticket) result;

    return new ScanTicketOutput(ticket);
  }

  private Ticket parseTicket(ScanTicketInput input) {
    CompletableFuture<ReceiptV5Document> parseTicketContentFuture = parseTicketContentAsync(input);
    CompletableFuture<String> uploadTicketToStorageFuture = uploadFileToStorageAsync(input);

    return CompletableFuture
        .allOf(parseTicketContentFuture, uploadTicketToStorageFuture)
        .thenApplyAsync(_ -> {
          ReceiptV5Document parsedTicketDocument = parseTicketContentFuture.join();
          String uploadedTicketUrl = uploadTicketToStorageFuture.join();
          return buildTicket(input, parsedTicketDocument, uploadedTicketUrl);
        })
        .thenComposeAsync(this::storeTicketInDatabase)
        .join();
  }

  // region Futures

  private Ticket buildTicket(ScanTicketInput input, ReceiptV5Document receiptDocument, String uploadedTicketUrl) {
    Ticket ticket = MindeeAdapter.toTicket(receiptDocument);
    TicketMetadata metadata = generateTicketMetadata(input, receiptDocument, uploadedTicketUrl);
    ticket.setMetadata(metadata);

    return ticket;
  }

  @Async("taskExecutor")
  private CompletableFuture<ReceiptV5Document> parseTicketContentAsync(ScanTicketInput input) {
    LocalInputSource imageSource = new LocalInputSource(input.imageFile(), input.fileName());

    return CompletableFuture.supplyAsync(doParseTicketContent(imageSource));
  }

  private Supplier<ReceiptV5Document> doParseTicketContent(LocalInputSource imageSource) {
    return () -> {
      log.debug("Parsing ticket content asynchronously...");

      ReceiptV5Document receiptDocument;
      try {
        InferenceParameters inferenceParameters = InferenceParameters.builder()
            .
            .build();
        var predict = mindee.enqueueAndGetInference(imageSource, inferenceParameters)
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

  private CompletableFuture<Ticket> storeTicketInDatabase(Ticket ticket) {
    return CompletableFuture.supplyAsync(() -> {
      log.debug("Saving ticket in database...");
      try {
        return ticketRepository.save(ticket);
      } catch (PersistenceErrorException e) {
        String message = "Error saving ticket in database";
        log.error(message);
        throw new CompletionException(e);
      }
    });
  }

  @Async("taskExecutor")
  private CompletableFuture<String> uploadFileToStorageAsync(ScanTicketInput input) {
    String ticketPathInBucket = "tickets/%s/%d_%s".formatted(
        input.userId(), input.scanTimestamp().getTime(), input.fileName()
    );

    return CompletableFuture.supplyAsync(doUploadTicketToStorage(ticketPathInBucket, input.imageFile()));
  }

  // endregion Futures

  private Supplier<String> doUploadTicketToStorage(String ticketPath, byte[] imageBytes) {
    return () -> {
      log.debug("Uploading ticket to Storage");

      String uploadedFileUrl = null;
      File imageTempFile = null;
      try {
        imageTempFile = toFile(imageBytes);
        uploadedFileUrl = storageService.uploadFile(ticketPath, imageTempFile);
        log.debug("Ticket uploaded to Storage");
      } catch (PersistenceErrorException e) {
        throw new RuntimeException(e);
      } finally {
        if (imageTempFile != null) {
          imageTempFile.delete();
        }
      }

      return uploadedFileUrl;
    };
  }

  private File toFile(byte[] bytes) {
    try {
      File tempFile = File.createTempFile("tempFile-", ".tmp");
      FileCopyUtils.copy(bytes, tempFile);
      return tempFile;
    } catch (IOException e) {
      String message = "Could not create temporary file";
      log.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private TicketMetadata generateTicketMetadata(ScanTicketInput input, ReceiptV5Document document, String imageUri) {
    return TicketMetadata.builder()
        .rawOcrResponse(document.toString())
        .scanTimestamp(input.scanTimestamp())
        .uploadedImageUri(imageUri)
        .build();
  }

}
