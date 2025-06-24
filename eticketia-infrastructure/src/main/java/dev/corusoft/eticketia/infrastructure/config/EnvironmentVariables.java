package dev.corusoft.eticketia.infrastructure.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Configurable environment varaibles for the project.
 */
@RequiredArgsConstructor
public enum EnvironmentVariables {
  FIREBASE_CREDENTIALS("FIREBASE_CREDENTIALS", true);

  private static final String ENCODED_SUFFIX = ".ENCODED";
  /**
   * Name of the property in the {@code .*.env} file.
   */
  private final String propertyName;
  /**
   * Property contains the suffix {@code .encoded} in its name.
   */
  @Getter
  private final boolean isEncoded;

  public String getPropertyName() {
    return (!isEncoded) ? propertyName : propertyName + ENCODED_SUFFIX;
  }
}
