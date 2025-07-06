package dev.corusoft.eticketia.application.usecases.auth;

import dev.corusoft.eticketia.domain.entities.roles.RoleName;
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

  public static final RoleName DEFAULT_NEW_USER_ROLE = RoleName.BASIC;
}
