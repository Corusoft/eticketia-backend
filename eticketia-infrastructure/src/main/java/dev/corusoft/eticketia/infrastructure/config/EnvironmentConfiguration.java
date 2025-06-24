package dev.corusoft.eticketia.infrastructure.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Read content from environment variables defined in {@code .env} files.
 */
@Log4j2
@Configuration
@RequiredArgsConstructor
@PropertySource(
    value = "file:${user.dir}/.${spring.profiles.active}.env",
    ignoreResourceNotFound = true
)
public class EnvironmentConfiguration {
  private final Environment environment;

  /**
   * Get value of customized environment value. If property is encoded, returns the decoded value.
   *
   * @param property Customized property.
   * @return Value of the property
   */
  public String getProperty(EnvironmentVariables property) {
    if (!property.isEncoded()) {
      return getProperty(property.getPropertyName());
    }

    // Decode value content
    String value = getProperty(property.getPropertyName());
    byte[] propertyBytes = Base64.getDecoder().decode(value);
    return new String(propertyBytes, UTF_8);
  }

  /**
   * Returns the value of the given {@code variableName}.
   *
   * @param variableName Name of the variable to read
   * @return Content of the variable
   */
  public String getProperty(String variableName) {
    String property = environment.getProperty(variableName);
    if (property == null) {
      String message = "Environment variable '%s' does not exist".formatted(variableName);
      log.error(message);
      throw new IllegalArgumentException(message);
    }

    return property;
  }
}
