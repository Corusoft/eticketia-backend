package dev.corusoft.eticketia.domain.exceptions.ticket;

import dev.corusoft.eticketia.domain.exceptions.DomainException;

/**
 * Thrown when the ticket image could not be uploaded to the storage service.
 */
public class UnableToPersistTicketFileException extends DomainException {

  private static final String KEY = UnableToPersistTicketFileException.class.getName();

  public UnableToPersistTicketFileException() {
    super(KEY);
  }

}
