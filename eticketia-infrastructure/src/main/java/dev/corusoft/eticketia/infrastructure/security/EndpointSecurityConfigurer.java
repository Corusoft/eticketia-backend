package dev.corusoft.eticketia.infrastructure.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Interface to indicate a RestController that it must configure who can access each of the
 * endpoints it is exposing
 */
public interface EndpointSecurityConfigurer {

  /**
   * Configure the current controller endpoints security
   */
  void secureEndpoints(HttpSecurity httpSecurity) throws Exception;
}
