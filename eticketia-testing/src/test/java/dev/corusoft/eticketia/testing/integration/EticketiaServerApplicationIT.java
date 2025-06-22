package dev.corusoft.eticketia.testing.integration;

import static org.assertj.core.api.Assertions.assertThat;

import dev.corusoft.eticketia.server.EticketiaServerApplication;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@Log4j2
@SpringBootTest(classes = {EticketiaServerApplication.class})
class EticketiaServerApplicationIT extends AbstractTestNGSpringContextTests {

  @Autowired
  private EticketiaServerApplication serverApplication;

  @Test
  void springContextLoadsForIntegrationTests() {
    assertThat(serverApplication).isNotNull();
    log.debug("Application context for integration testing  loaded succesfuly");
  }
}