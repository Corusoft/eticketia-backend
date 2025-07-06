package dev.corusoft.eticketia.domain.exceptions.auth;

import dev.corusoft.eticketia.domain.exceptions.DomainException;

/**
 * Thrown when attepmting to register a user with an email that is already registered.
 */
public class EmailAlreadyRegisteredException extends DomainException {
  private static final String KEY = EmailAlreadyRegisteredException.class.getName();

  /**
   * Thrown when the given {@code email} is already registered.
   *
   * @param email Email already registered
   */
  public EmailAlreadyRegisteredException(String email) {
    this(email, null);
  }

  /**
   * Thrown when the given {@code email} is already registered.
   *
   * @param email Email already registered
   * @param cause Details of the exception
   */
  public EmailAlreadyRegisteredException(String email, Throwable cause) {
    super(KEY, new Object[] {email}, cause);
  }
}
