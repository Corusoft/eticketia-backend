package dev.corusoft.eticketia.server;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application entry point.
 */
@ComponentScan(basePackages = {"dev.corusoft.eticketia"})
@SpringBootApplication
@Log4j2
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