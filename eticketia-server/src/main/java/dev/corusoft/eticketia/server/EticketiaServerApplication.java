package dev.corusoft.eticketia.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 */
@SpringBootApplication
public class EticketiaServerApplication {

  /**
   * Runs the application.
   *
   * @param args CLI arguments, if any
   */
  public static void main(String[] args) {
    SpringApplication.run(EticketiaServerApplication.class, args);
  }
}