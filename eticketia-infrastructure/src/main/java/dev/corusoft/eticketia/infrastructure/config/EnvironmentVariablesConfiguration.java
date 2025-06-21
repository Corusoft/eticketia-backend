package dev.corusoft.eticketia.infrastructure.config;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Log4j2
@Getter
@Configuration
@PropertySource(
    value = "file:${user.dir}/.${spring.profiles.active}.env",
    ignoreResourceNotFound = true
)
public class EnvironmentVariablesConfiguration {

  private static final String FILE_EXTENSION = ".env";

  @Value("${user.dir}")
  private String userDir;

  @Value("${spring.profiles.active}")
  private String activeProfile;

  // region Properties from file
    /* Example of reading property from .env file
    @Value("${PROPERTY_NAME}")
    private String propertyName;
     */

  // endregion

  @PostConstruct
  public void init() {
    final String ENV_FILE_PATH = Paths.get(userDir, "." + activeProfile + FILE_EXTENSION)
        .toString();

    File envFile = new File(ENV_FILE_PATH);
    if (!envFile.exists()) {
      log.warn(
          "The configuration file '{}' was not found. Environmental variables will be loaded from default settings.",
          envFile.getPath());
      return;
    }

    log.debug("Environmental configuration loaded from '{}'.", ENV_FILE_PATH);
  }
}
