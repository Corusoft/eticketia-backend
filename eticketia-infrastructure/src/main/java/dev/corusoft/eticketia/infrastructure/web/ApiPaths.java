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

  // region Ticket
  public static final String BASE_TICKET = "/tickets";

  public static final String TICKET_SCAN = BASE_TICKET + "/scan";
  public static final String TICKET_BY_ID_FORMAT = BASE_TICKET + "/{%s}";
  public static final String TICKET_BY_ID = TICKET_BY_ID_FORMAT.formatted("id");

  // endregion Ticket

  // region User
  public static final String BASE_USER = "/users";

  public static final String USER_BY_ID_FORMAT = BASE_USER + "/{%s}";
  public static final String USER_BY_ID = USER_BY_ID_FORMAT.formatted("id");

  // endregion User
}
