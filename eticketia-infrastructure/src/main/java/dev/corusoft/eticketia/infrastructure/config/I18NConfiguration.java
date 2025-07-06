package dev.corusoft.eticketia.infrastructure.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configure aspects related with I18N and translations
 */
@Configuration
public class I18NConfiguration {
  private static final Locale DEFAULT_LOCALE = Locale.of("es");
  private static final List<Locale> SUPPORTED_LOCALES = List.of(
      DEFAULT_LOCALE,
      Locale.of("en")
  );
  private static final String[] APPLICATION_MODULES = {
      "domain",
      "application",
      "infrastructure",
      "server"
  };

  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver headerLocaleResolver = new AcceptHeaderLocaleResolver();
    headerLocaleResolver.setSupportedLocales(SUPPORTED_LOCALES);
    headerLocaleResolver.setDefaultLocale(DEFAULT_LOCALE);

    return headerLocaleResolver;
  }

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
    source.setDefaultEncoding("UTF-8");
    source.setDefaultLocale(DEFAULT_LOCALE);
    // Configure basenames targeting i18n/<module> directory for each module
    source.setBasenames(getBasenames().toArray(new String[0]));

    return source;
  }

  private List<String> getBasenames() {
    return Arrays.stream(APPLICATION_MODULES)
        .map("classpath:i18n/%s/messages"::formatted)
        .toList();
  }


}
