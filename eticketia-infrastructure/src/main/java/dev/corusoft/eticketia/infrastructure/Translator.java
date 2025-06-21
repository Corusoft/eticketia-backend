package dev.corusoft.eticketia.infrastructure;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Translator {

  private final MessageSource messageSource;

  public String generateMessage(String exceptionKey, Locale locale) {
    return generateMessage(exceptionKey, null, locale);
  }

  public String generateMessage(String exceptionKey, Object[] args, Locale locale) {
    return messageSource.getMessage(exceptionKey, args, exceptionKey, locale);
  }

}
