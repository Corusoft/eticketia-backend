package dev.corusoft.eticketia.infrastructure.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {
  // region HTTP
  public static final String PREFIX_BEARER_TOKEN = "Bearer ";
  // endregion HTTP

  // region AUTHENTICATION
  public static final String TOKEN_ATTRIBUTE_NAME = "token";
  public static final String USER_ID_ATTRIBUTE_NAME = "uid";
  public static final String USER_ROLES_CLAIM = "roles";
  // endregion AUTHENTICATION
}
