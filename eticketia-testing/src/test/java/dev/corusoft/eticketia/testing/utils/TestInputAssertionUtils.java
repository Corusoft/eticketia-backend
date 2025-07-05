package dev.corusoft.eticketia.testing.utils;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import dev.corusoft.eticketia.application.UseCaseInput;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mockito.ArgumentCaptor;

/**
 * Utility class to make assertions over the arguments passed to tests.
 */
@RequiredArgsConstructor(access = PRIVATE)
@Log4j2
public final class TestInputAssertionUtils {

  /**
   * Asserts that the given object {@code actual} values matches with the {@code expected} values.
   * <br>
   * Value of {@code actual} can be captured using an {@link ArgumentCaptor}.
   *
   * <pre>
   * <code>ArgumentCaptor&ltT&gt captor = ArgumentCaptor.forClass(T.class);
   * verify(&ltservice being mocked call&gt).&ltmethod being mocked&gt(captor.capture());
   * TestInputAssertionUtils.assertInputs(captor.getValue(), expectedValue);
   * </pre>
   * </code>
   *
   * @param actual   Object captured during the test.
   * @param expected {@link INPUT} object with the expected values
   * @param <INPUT>  Type of the input object being tested
   */
  public static <INPUT extends UseCaseInput> void assertInputs(Object actual, INPUT expected) {
    assertThat(actual).isNotNull();
    assertThat(expected).isNotNull();

    RecordComponent[] inputAttributes = expected.getClass().getRecordComponents();
    List<String> fieldNames = new ArrayList<>();
    List<Object> expectedValues = new ArrayList<>();

    // Extract expected values
    try {
      for (RecordComponent inputAttribute : inputAttributes) {
        fieldNames.add(inputAttribute.getName());
        expectedValues.add(inputAttribute.getAccessor().invoke(expected));
      }
    } catch (InvocationTargetException | IllegalAccessException e) {
      String message = "Failed to extract attributes during test execution";
      log.error(message, e);
      throw new RuntimeException(message, e);
    }

    // Assert input values match with the expected
    assertThat(fieldNames).isNotEmpty();
    assertThat(actual)
        .extracting(fieldNames.toArray(new String[0]))
        .containsExactly(expectedValues.toArray());
  }
}
