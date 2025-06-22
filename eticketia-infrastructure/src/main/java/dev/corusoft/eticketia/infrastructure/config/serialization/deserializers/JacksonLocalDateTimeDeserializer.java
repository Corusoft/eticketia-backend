package dev.corusoft.eticketia.infrastructure.config.serialization.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Deserializer for {@link LocalDateTime} using {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
 */
public class JacksonLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  /**
   * Intanciate the deserializer for type {@link LocalDateTime}.
   */
  public JacksonLocalDateTimeDeserializer() {
    super(LocalDateTime.class);
  }

  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String dateTimeString = p.getValueAsString();

    return LocalDateTime.parse(dateTimeString, formatter);
  }
}
