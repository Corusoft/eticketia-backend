package dev.corusoft.eticketia.domain.exceptions.auth;

import dev.corusoft.eticketia.domain.exceptions.DomainException;

/**
 * Thrown when attempting to register a user that already exists.
 */
public class UserAlreadyExistsException extends DomainException {
  private static final String KEY = UserAlreadyExistsException.class.getName();

  /**
   * Thrown when a user with the given {@code uid} already exists
   *
   * @param uid User ID
   */
  public UserAlreadyExistsException(String uid) {
    this(uid, null);
  }

  /**
   * Thrown when a user with the given {@code uid} already exists
   *
   * @param uid   User ID
   * @param cause Details of the exception
   */
  public UserAlreadyExistsException(String uid, Throwable cause) {
    super(KEY, new Object[] {uid}, cause);
  }
}
