package dev.corusoft.eticketia.testing.integration.services;

import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.DEFAULT_NEW_USER_ROLE;
import static dev.corusoft.eticketia.infrastructure.security.SecurityConstants.USER_ROLES_CLAIM;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockFirebaseAuthException;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockUserMetadata;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockUserRecord;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserMetadata;
import com.google.firebase.auth.UserRecord;
import dev.corusoft.eticketia.testing.utils.TimeUtils;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Factory to data instances used by {@link FirebaseAuth}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FirebaseAuthMockDataFactory {

  public static FirebaseAuthException createFirebaseAuthException(AuthErrorCode errorCode) {
    FirebaseAuthException mockedException = mockFirebaseAuthException();
    when(mockedException.getAuthErrorCode()).thenReturn(errorCode);

    return mockedException;
  }

  public static UserRecord createMockedUserRecord(String uid, String email, String nickname) {
    // Configure default user metadata
    var userMetadata = createUsermetadata();

    return createMockedUserRecord(uid, email, nickname, userMetadata);
  }

  public static UserMetadata createUsermetadata() {
    long currentMillis = TimeUtils.currentUtcMilliseconds();

    var mockedUserMetadata = mockUserMetadata();
    when(mockedUserMetadata.getCreationTimestamp()).thenReturn(currentMillis);

    return mockedUserMetadata;
  }

  public static UserRecord createMockedUserRecord(String uid, String email, String nickname,
                                                  UserMetadata userMetadata) {
    UserRecord mockedUserRecord = mockUserRecord();

    // Configure user record
    when(mockedUserRecord.getUid()).thenReturn(uid);
    when(mockedUserRecord.getEmail()).thenReturn(email);
    when(mockedUserRecord.getDisplayName()).thenReturn(nickname);

    if (Objects.nonNull(userMetadata)) {
      when(mockedUserRecord.getUserMetadata()).thenReturn(userMetadata);
    }

    // Configure custom claims
    Map<String, Object> customClaims = Map.of(USER_ROLES_CLAIM, DEFAULT_NEW_USER_ROLE.getName());
    when(mockedUserRecord.getCustomClaims()).thenReturn(customClaims);

    return mockedUserRecord;
  }
}
