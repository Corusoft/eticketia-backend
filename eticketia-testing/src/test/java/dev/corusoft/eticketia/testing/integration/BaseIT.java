package dev.corusoft.eticketia.testing.integration;

import com.github.javafaker.Faker;
import dev.corusoft.eticketia.testing.beans.FakerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * Base class for light integration tests that do not require the whole Spring Context to be loaded.<br>
 * Configure the necessary beans with the {@link ContextConfiguration} class.
 */
@ContextConfiguration(
    classes = {FakerBean.class}
)
public abstract class BaseIT extends AbstractTestNGSpringContextTests {
  @Autowired
  protected Faker faker;
}
