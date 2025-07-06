package dev.corusoft.eticketia.infrastructure;

import dev.corusoft.eticketia.domain.exceptions.DomainException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Translator {
  private static final String NO_TRANSLATION_AVAILABLE_KEY =
      Translator.class.getName() + ".no_translation_available";
  private static final String NO_TRANSLATION_AVAILABLE_MESSAGE = "no translation available";

  private final MessageSource messageSource;

  /**
   * Returns the translation for the given {@link DomainException}.
   *
   * @param e Exception to translate
   * @return The translated message for the given exception
   */
  public String generateMessage(DomainException e) {
    return generateMessage(e.getI18nKey(), e.getI18nArgs());
  }

  /**
   * Returns the translation for the given {@code key} using the given {@code locale}.
   *
   * @param key  Key of the message to translate
   * @param args Values for the placeholders in the message template
   * @return The translated message
   */
  public String generateMessage(String key, Object[] args) {
    return generateMessage(key, args, null);
  }

  /**
   * Returns the translation for the given {@code key} using the given {@code locale}.<br>
   * Placeholders in the message template will be replaced in order by each item of {@code args}.
   *
   * @param key    Key of the message to translate
   * @param locale Locale to use for the translation
   * @param args   Values for the placeholders in the message template
   * @return The translated message
   */
  public String generateMessage(String key, Object[] args, Locale locale) {
    if (locale == null) {
      locale = LocaleContextHolder.getLocale();
    }

    // Translate the message with the given key
    String message = messageSource.getMessage(key, args, null, locale);

    // If no translation available, return a fallback message
    if (message == null) {
      log.warn("No translation found for key '{}'. Generating fallback message", key);
      return generateFallbackMessage(key, locale);
    }

    return message;
  }

  private String generateFallbackMessage(String key, Locale locale) {
    Object[] args = new Object[] {key};

    String fallbackMessage = messageSource.getMessage(
        NO_TRANSLATION_AVAILABLE_KEY, args, null, locale
    );

    if (fallbackMessage == null) {
      log.warn("No fallback translation was found");
    }

    return fallbackMessage;
  }

  /**
   * Returns the translation for the given {@code key}.
   *
   * @param key Key of the message to translate
   * @return The translated message
   */
  public String generateMessage(String key) {
    return generateMessage(key, null);
  }

}
