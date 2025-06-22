package dev.corusoft.eticketia.infrastructure.config.serialization.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Deserializer for {@link LocalDate} using {@link DateTimeFormatter#ISO_LOCAL_DATE}.
 */
public class JacksonLocalDateDeserializer extends StdDeserializer<LocalDate> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

  /**
   * Intanciate the deserializer for type {@link LocalDate}.
   */
  public JacksonLocalDateDeserializer() {
    super(LocalDate.class);
  }

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String dateString = p.getValueAsString();

    return LocalDate.parse(dateString, formatter);
  }
}
