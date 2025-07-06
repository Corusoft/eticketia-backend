package dev.corusoft.eticketia.infrastructure.services.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import dev.corusoft.eticketia.infrastructure.config.EnvironmentConfiguration;
import dev.corusoft.eticketia.infrastructure.config.EnvironmentVariables;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to initialize Firebase configuration.
 */
@Log4j2
@RequiredArgsConstructor
@Configuration
public class FirebaseConfiguration {
  private final EnvironmentConfiguration env;
  private FirebaseApp firebaseApp;

  /**
   * Initialize Firebase Application using file credentials.
   */
  @PostConstruct
  public void initFirebaseApp() throws IOException {
    String applicationName = env.getProperty("spring.application.name");

    String firebaseCredentials = env.getProperty(EnvironmentVariables.FIREBASE_CREDENTIALS);
    InputStream credentialStreams = new ByteArrayInputStream(firebaseCredentials.getBytes());

    GoogleCredentials credentials = GoogleCredentials.fromStream(credentialStreams);
    FirebaseOptions firebaseOptions = FirebaseOptions.builder()
        .setCredentials(credentials)
        .build();

    // Avoid multiple initializations
    if (FirebaseApp.getApps().isEmpty()) {
      firebaseApp = FirebaseApp.initializeApp(firebaseOptions, applicationName);
      log.debug("Firebase App '{}' initialized", firebaseApp.getName());
    }
  }

  @Bean
  public FirebaseAuth firebaseAuth() {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
    log.debug("Firebase Auth '{}' initialized",
        firebaseApp.getName()
    );

    return firebaseAuth;
  }
}
