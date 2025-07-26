package dev.corusoft.eticketia.infrastructure.repositories.tickets;

import dev.corusoft.eticketia.domain.entities.tickets.Ticket;
import dev.corusoft.eticketia.domain.repositories.tickets.TicketRepository;
import dev.corusoft.eticketia.infrastructure.repositories.AbstractFirestoreRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@Lazy
public class FirebaseTicketRepository
    extends AbstractFirestoreRepository<Ticket>
    implements TicketRepository {

  private static final String COLLECTION_NAME = "tickets";

  public FirebaseTicketRepository() {
    super(Ticket.class, COLLECTION_NAME);
  }

}
