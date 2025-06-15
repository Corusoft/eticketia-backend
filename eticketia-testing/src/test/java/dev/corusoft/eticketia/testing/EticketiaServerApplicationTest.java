package dev.corusoft.eticketia.testing;

import static org.assertj.core.api.Assertions.assertThat;

import dev.corusoft.eticketia.server.EticketiaServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {EticketiaServerApplication.class})
class EticketiaServerApplicationTest {

  @Autowired
  private EticketiaServerApplication serverApplication;

  @Test
  void contextLoads() {
    assertThat(serverApplication).isNotNull();
  }
}