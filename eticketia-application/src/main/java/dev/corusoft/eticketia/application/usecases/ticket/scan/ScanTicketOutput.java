package dev.corusoft.eticketia.application.usecases.ticket.scan;


import dev.corusoft.eticketia.application.UseCaseOutput;
import dev.corusoft.eticketia.domain.entities.tickets.Ticket;

/**
 * Ticket content after being scanned.
 */
public record ScanTicketOutput(
    Ticket ticket
) implements UseCaseOutput {

}
