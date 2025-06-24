package dev.corusoft.eticketia.domain.entities.roles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Name of the roles a user can have attached.
 */
@Getter
@RequiredArgsConstructor
public enum RoleName {
  ADMIN("ADMIN"),
  BASIC("BASIC"),
  PREMIUM("PREMIUM");

  private final String name;
}
