package dev.corusoft.eticketia.domain.repositories.tickets;

import dev.corusoft.eticketia.domain.entities.tickets.Ticket;
import dev.corusoft.eticketia.domain.repositories.PagingAndSortingRepository;

public interface TicketRepository extends PagingAndSortingRepository<Ticket, Long> {

}
