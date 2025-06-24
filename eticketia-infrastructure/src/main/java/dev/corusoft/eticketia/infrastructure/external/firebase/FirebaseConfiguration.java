package dev.corusoft.eticketia.infrastructure.external.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import dev.corusoft.eticketia.infrastructure.config.EnvironmentConfiguration;
import dev.corusoft.eticketia.infrastructure.config.EnvironmentVariables;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${spring.application.name}")
  private String applicationName;

  /**
   * Initialize Firebase Application using file credentials.
   */
  @PostConstruct
  public void initFirebaseApp() throws IOException {
    String firebaseCredentials = env.getProperty(EnvironmentVariables.FIREBASE_CREDENTIALS);
    InputStream credentialStreams = new ByteArrayInputStream(firebaseCredentials.getBytes());

    GoogleCredentials credentials = GoogleCredentials.fromStream(credentialStreams);
    FirebaseOptions firebaseOptions = FirebaseOptions.builder()
        .setCredentials(credentials)
        .build();

    // Avoid multiple initializations
    if (FirebaseApp.getApps().isEmpty()) {
      firebaseApp = FirebaseApp.initializeApp(firebaseOptions, applicationName);
      log.debug("Firebase application '{}' initialized succesfuly", firebaseApp.getName());
    }
  }
}
