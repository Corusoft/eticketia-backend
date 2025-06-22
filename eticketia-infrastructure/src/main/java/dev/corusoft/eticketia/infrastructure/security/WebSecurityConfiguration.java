package dev.corusoft.eticketia.infrastructure.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
  /**
   * List that holds the endpoint access configurations for each Rest controller
   */
  private final List<EndpointSecurityConfigurer> endpointSecurityConfigurersList;

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http
        .getSharedObject(AuthenticationManagerBuilder.class);

    return authenticationManagerBuilder.build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        // Disable CSRF as it not being used (CSRF only affects Sessions and Cookies, not JWT)
        .csrf(AbstractHttpConfigurer::disable)
        // Do not store user Sessions (Stateless API)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    configureFilters(http);

    // Apply the security rules configured by each REST controller
    secureEndpoints(http);

    return http.build();
  }

  private void configureFilters(HttpSecurity http) {
    // Apply JWT custom filtering
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
}
