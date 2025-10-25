package dev.corusoft.eticketia.infrastructure.services.mindee;

import com.mindee.MindeeClientV2;
import dev.corusoft.eticketia.infrastructure.config.EnvironmentConfiguration;
import dev.corusoft.eticketia.infrastructure.config.EnvironmentVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to initialize Mindee configuration.
 */
@Log4j2
@RequiredArgsConstructor
@Configuration
public class MindeeConfiguration {

  private final EnvironmentConfiguration env;


  @Bean
  public MindeeClientV2 mindeeClient() {
    String mindeeApiKey = env.getProperty(EnvironmentVariables.MINDEE_API_KEY);

    return new MindeeClientV2(mindeeApiKey);
  }
}
