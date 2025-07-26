package dev.corusoft.eticketia.application.usecases.ticket.scan;

import dev.corusoft.eticketia.application.UseCaseInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.util.Date;

/**
 * Parameters to scan a ticket
 *
 * @param imageFile     Ticket image as bytes
 * @param scanTimestamp Timestamp when the ticket was scanned
 */
public record ScanTicketInput(
    @NotNull byte[] imageFile,
    @NotNull @PastOrPresent Date scanTimestamp,
    @NotBlank String userId
) implements UseCaseInput {

}
