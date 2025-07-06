package dev.corusoft.eticketia.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface to mark custom Spring Security filters.
 */
public interface CustomSecurityFilter {

  /**
   * Checks if the given request can be handled by the filter.
   *
   * @param req Received request
   * @return True if the request can be handled by the filter, false otherwise
   */
  boolean canApplyFilter(HttpServletRequest req);
}
