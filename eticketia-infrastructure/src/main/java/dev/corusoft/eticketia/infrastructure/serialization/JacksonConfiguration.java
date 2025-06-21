package dev.corusoft.eticketia.infrastructure.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.corusoft.eticketia.infrastructure.serialization.deserializers.JacksonLocalDateDeserializer;
import dev.corusoft.eticketia.infrastructure.serialization.deserializers.JacksonLocalDateTimeDeserializer;
import dev.corusoft.eticketia.infrastructure.serialization.serializers.JacksonLocalDateSerializer;
import dev.corusoft.eticketia.infrastructure.serialization.serializers.JacksonLocalDateTimeSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JacksonConfiguration {

  @Bean
  @Primary
  public static ObjectMapper objectMapper() {
    JavaTimeModule javaTimeModule = configureTimeModules();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(javaTimeModule);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    return objectMapper;
  }

  public static JavaTimeModule configureTimeModules() {
    JavaTimeModule module = new JavaTimeModule();
    addSerializers(module);
    addDeserializers(module);
    return module;
  }

  private static void addSerializers(JavaTimeModule module) {
    module.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
    module.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
  }

  private static void addDeserializers(JavaTimeModule module) {
    module.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
    module.addDeserializer(LocalDateTime.class, new JacksonLocalDateTimeDeserializer());
  }
}
