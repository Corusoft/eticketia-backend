package dev.corusoft.eticketia.application.usecases.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class to define auth related constraints.
 * E.g.: password lengths.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstraints {
  public static final int MIN_PASSWORD_LENGTH = 6;
  public static final int MAX_PASSWORD_LENGTH = 50;
}
