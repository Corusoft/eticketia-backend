package dev.corusoft.eticketia.testing.integration;

import static org.assertj.core.api.Assertions.assertThat;

import dev.corusoft.eticketia.server.EticketiaServerApplication;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest(classes = {EticketiaServerApplication.class})
class EticketiaServerApplicationIntegrationTest {

  private final EticketiaServerApplication serverApplication;

  @Autowired
  public EticketiaServerApplicationIntegrationTest(EticketiaServerApplication serverApplication) {
    this.serverApplication = serverApplication;
  }

  @Test
  void springContextLoadsForIntegrationTesting() {
    assertThat(serverApplication).isNotNull();
    log.debug("Application context loaded succesfuly for integration testing environment.");
  }
}
