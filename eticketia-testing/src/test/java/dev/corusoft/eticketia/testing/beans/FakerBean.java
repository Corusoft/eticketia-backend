package dev.corusoft.eticketia.testing.beans;

import com.github.javafaker.Faker;
import java.util.Locale;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Bean;

@TestComponent
public class FakerBean {
  private static final Locale LOCALE = Locale.getDefault();

  /**
   * Configures the instance of @{@link Faker}.
   */
  @Bean
  public Faker faker() {
    return new Faker(LOCALE);
  }
}
