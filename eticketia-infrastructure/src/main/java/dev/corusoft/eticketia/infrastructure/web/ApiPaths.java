package dev.corusoft.eticketia.infrastructure.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Defines the base paths for all the controllers of the application.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiPaths {
  // region Auth
  public static final String BASE_AUTH = "/auth";

  public static final String AUTH_SIGNUP = BASE_AUTH + "/signup";

  // endregion Auth

}
