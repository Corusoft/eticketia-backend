package dev.corusoft.eticketia.infrastructure.config.serialization.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Serializer for {@link LocalDate} using {@link DateTimeFormatter#ISO_LOCAL_DATE}.
 */
public class JacksonLocalDateSerializer extends StdSerializer<LocalDate> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

  /**
   * Intanciate the serializer for type {@link LocalDate}.
   */
  public JacksonLocalDateSerializer() {
    super(LocalDate.class);
  }

  @Override
  public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    String dateString = formatter.format(value);
    gen.writeString(dateString);
  }
}
