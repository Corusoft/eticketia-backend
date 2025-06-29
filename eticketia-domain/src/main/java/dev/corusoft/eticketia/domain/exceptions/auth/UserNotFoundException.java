package dev.corusoft.eticketia.domain.exceptions.auth;

import dev.corusoft.eticketia.domain.exceptions.DomainException;

/**
 * Thrown when attempting to find a user that does not exist.
 */
public class UserNotFoundException extends DomainException {
  private static final String KEY = UserNotFoundException.class.getName();

  /**
   * Thrown when attempting to find a user by the given {@code uid} that does not exist.
   *
   * @param uid User ID
   */
  public UserNotFoundException(String uid) {
    this(uid, null);
  }

  /**
   * Thrown when attempting to find a user by the given {@code uid} that does not exist.
   *
   * @param uid   User ID
   * @param cause Details of the exception
   */
  public UserNotFoundException(String uid, Throwable cause) {
    super(KEY, new Object[] {uid}, cause);
  }
}
