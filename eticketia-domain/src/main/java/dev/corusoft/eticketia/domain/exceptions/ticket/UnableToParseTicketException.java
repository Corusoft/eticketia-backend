package dev.corusoft.eticketia.domain.exceptions.ticket;

import dev.corusoft.eticketia.domain.exceptions.DomainException;

/**
 * Thrown when attempting to parse a ticket image with the Mindee API.
 */
public class UnableToParseTicketException extends DomainException {

  private static final String KEY = UnableToParseTicketException.class.getName();

  public UnableToParseTicketException() {
    super(KEY);
  }
}
