package dev.corusoft.eticketia.infrastructure.services.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import dev.corusoft.eticketia.infrastructure.config.EnvironmentConfiguration;
import dev.corusoft.eticketia.infrastructure.config.EnvironmentVariables;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Configuration class to initialize Firebase configuration.
 */
@Log4j2
@RequiredArgsConstructor
@Configuration
public class FirebaseConfiguration {

  private final EnvironmentConfiguration env;

  /**
   * Initialize the {@link FirebaseApp}.
   *
   * @throws IOException if credentials cannot be read.
   */
  @Lazy
  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    String applicationName = env.getProperty("spring.application.name");

    Optional<FirebaseApp> existingApp = FirebaseApp.getApps().stream()
        .filter(app -> app.getName().equals(applicationName))
        .findFirst();

    if (existingApp.isPresent()) {
      log.debug("Firebase App '{}' already initialized.", applicationName);
      return existingApp.get();
    }

    String firebaseCredentials = env.getProperty(EnvironmentVariables.FIREBASE_CREDENTIALS);
    InputStream credentialStreams = new ByteArrayInputStream(firebaseCredentials.getBytes());

    GoogleCredentials credentials = GoogleCredentials.fromStream(credentialStreams);
    FirebaseOptions firebaseOptions = FirebaseOptions.builder()
        .setCredentials(credentials)
        .build();
    FirebaseApp initializedApp = FirebaseApp.initializeApp(firebaseOptions, applicationName);
    log.debug("Firebase App '{}' initialized", initializedApp.getName());

    return initializedApp;
  }

  /**
   * Initialize the {@link FirebaseAuth}.
   */
  @Lazy
  @Bean
  public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
    log.debug("Firebase Auth '{}' initialized", firebaseApp.getName());

    return firebaseAuth;
  }

  /**
   * Initialize the {@link Firestore} database.
   */
  @Lazy
  @Bean
  public Firestore firestore(FirebaseApp firebaseApp) {
    Firestore firestore = FirestoreClient.getFirestore(firebaseApp);
    log.debug("Firebase Firestore database initialized");

    return firestore;
  }
}
