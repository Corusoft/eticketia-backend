package dev.corusoft.eticketia.application.usecases.ticket.scan;

import dev.corusoft.eticketia.application.UseCaseInput;
import java.util.Date;

/**
 * Parameters to scan a ticket
 *
 * @param imageFile     Ticket image as bytes
 * @param scanTimestamp Timestamp when the ticket was scanned
 */
public record ScanTicketInput(
    byte[] imageFile,
    Date scanTimestamp,
    String userId
) implements UseCaseInput {

}
