package dev.corusoft.eticketia.infrastructure.config;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Confogures aspects related with I18N and translations
 */
@Configuration
@RequiredArgsConstructor
public class I18NConfiguration {
  private static final Locale DEFAULT_LOCALE = Locale.of("es");
  private static final List<Locale> SUPPORTED_LOCALES = List.of(
      Locale.of("es"),
      Locale.of("en")
  );

  @Bean
  public static MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();

    // Configure basenames targeting i18n directory for each module
    messageSource.setBasenames(getBasenames());
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setDefaultLocale(DEFAULT_LOCALE);

    return messageSource;
  }

  private static String[] getBasenames() {
    String[] modules = {"domain", "application", "infrastructure", "server"};
    return Stream.of(modules)
        .map("classpath:i18n/%s/messages"::formatted)
        .toArray(String[]::new);
  }

  @Bean
  public static LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver headerLocaleResolver = new AcceptHeaderLocaleResolver();
    headerLocaleResolver.setSupportedLocales(SUPPORTED_LOCALES);
    headerLocaleResolver.setDefaultLocale(DEFAULT_LOCALE);

    return headerLocaleResolver;
  }
}
