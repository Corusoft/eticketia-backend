package dev.corusoft.eticketia.infrastructure.security;

import dev.corusoft.eticketia.infrastructure.security.filters.FirebaseTokenAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class WebSecurityConfiguration {

  /**
   * List that holds the endpoint access configurations for each Rest controller
   */
  private final List<EndpointSecurityConfigurer> endpointSecurityConfigurersList;
  /**
   * List of all the custom security filters to be applied on each request
   */
  private final List<CustomSecurityFilter> customSecurityFilters;

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http
        .getSharedObject(AuthenticationManagerBuilder.class);

    return authenticationManagerBuilder.build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    configureFilters(http);
    configureCsrf(http);
    configureSessions(http);

    // Apply the security rules configured by each REST controller
    secureEndpoints(http);

    return http.build();
  }

  private void configureFilters(HttpSecurity http) {
    FirebaseTokenAuthenticationFilter firebaseFilter =
        getRegisteredFilter(FirebaseTokenAuthenticationFilter.class);
    http.addFilterBefore(firebaseFilter, UsernamePasswordAuthenticationFilter.class);
  }

  private static HttpSecurity configureCsrf(HttpSecurity http) throws Exception {
    return http
        // Disable CSRF as it not being used (CSRF only affects Sessions and Cookies, not JWT)
        .csrf(AbstractHttpConfigurer::disable);
  }

  private static HttpSecurity configureSessions(HttpSecurity http) throws Exception {
    return http
        // Do not store user Sessions (Stateless API)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
  }

  private void secureEndpoints(HttpSecurity http) throws Exception {
    for (EndpointSecurityConfigurer configurer : endpointSecurityConfigurersList) {
      configurer.secureEndpoints(http);
    }

    // Deny requests to endpoints that are not explicitly registered
    http.authorizeHttpRequests(
        requests -> requests.anyRequest().denyAll()
    );
  }

  private <T extends CustomSecurityFilter> T getRegisteredFilter(Class<T> filterClass) {
    return customSecurityFilters.stream()
        .filter(filterClass::isInstance)
        .map(filterClass::cast)
        .findFirst()
        .orElseThrow(() -> {
          String message = "Request filter '%s' is not registered".formatted(filterClass.getName());
          log.error(message);
          return new IllegalStateException(message);
        });
  }
}
