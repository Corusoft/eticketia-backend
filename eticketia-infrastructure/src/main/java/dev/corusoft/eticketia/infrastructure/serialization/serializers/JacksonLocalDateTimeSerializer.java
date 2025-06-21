package dev.corusoft.eticketia.infrastructure.serialization.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializer for {@link LocalDateTime} using {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
 */
public class JacksonLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  /**
   * Intanciate the serializer for type {@link LocalDateTime}.
   */
  public JacksonLocalDateTimeSerializer() {
    super(LocalDateTime.class);
  }

  @Override
  public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    String dateTimeString = formatter.format(value);
    gen.writeString(dateTimeString);
  }
}
