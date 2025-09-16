package dev.corusoft.eticketia.domain.exceptions;

public class PersistenceErrorException extends DomainException {

  private static final String KEY = PersistenceErrorException.class.getName();

  public PersistenceErrorException(String message) {
    this(message, null);
  }

  public PersistenceErrorException(String message, Throwable cause) {
    super(KEY, message, cause);
  }


}
