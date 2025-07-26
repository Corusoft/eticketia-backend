package dev.corusoft.eticketia.domain.exceptions;

public class PersistenceErrorException extends DomainException {

  private static final String KEY = PersistenceErrorException.class.getName();

  public PersistenceErrorException(Throwable cause, String message) {
    super(KEY, message, cause);
  }


}
