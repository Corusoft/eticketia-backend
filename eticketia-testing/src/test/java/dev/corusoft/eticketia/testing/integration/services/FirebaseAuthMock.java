package dev.corusoft.eticketia.testing.integration.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserMetadata;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class to configure mock implementations for {@link FirebaseAuth}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FirebaseAuthMock {

  public static FirebaseAuth mockFirebaseAuth() {
    return mock(FirebaseAuth.class);
  }

  public static FirebaseAuthException mockFirebaseAuthException() {
    return mock(FirebaseAuthException.class);
  }

  // region Entities mock

  public static UserRecord mockUserRecord() {
    return mock(UserRecord.class);
  }

  public static UserMetadata mockUserMetadata() {
    return mock(UserMetadata.class);
  }

  // endregion Entities mock

  // region API mock

  /**
   * Configures the mock to return a specific {@link UserRecord} when {@link FirebaseAuth#createUser(CreateRequest)} is called.
   *
   * @param mockAuth     The FirebaseAuth mock instance to configure.
   * @param dataToReturn The user to be returned.
   */
  public static void mockCreateUser(FirebaseAuth mockAuth, UserRecord dataToReturn)
      throws FirebaseAuthException {
    doReturn(dataToReturn)
        .when(mockAuth).createUser(any(CreateRequest.class));
  }

  /**
   * Configures the mock to throw the given {@link FirebaseAuthException} when {@link FirebaseAuth#createUser(CreateRequest)} is called.
   *
   * @param mockAuth         The FirebaseAuth mock instance to configure.
   * @param exceptionToThrow The exception to be thrown.
   */
  public static void mockCreateUserWithException(FirebaseAuth mockAuth,
                                                 FirebaseAuthException exceptionToThrow)
      throws FirebaseAuthException {
    doThrow(exceptionToThrow)
        .when(mockAuth).createUser(any(CreateRequest.class));
  }

  /**
   * Configures the mock to return a specific {@link UserRecord} when {@link FirebaseAuth#updateUser(UpdateRequest)} is called.
   *
   * @param mockAuth     The FirebaseAuth mock instance to configure.
   * @param dataToReturn The user to be returned.
   */
  public static void mockUpdateUser(FirebaseAuth mockAuth, UserRecord dataToReturn)
      throws FirebaseAuthException {
    doReturn(dataToReturn)
        .when(mockAuth).updateUser(any(UpdateRequest.class));
  }

  /**
   * Configures the mock to throw the given {@link FirebaseAuthException} when {@link FirebaseAuth#createUser(CreateRequest)} is called.
   *
   * @param mockAuth         The FirebaseAuth mock instance to configure.
   * @param exceptionToThrow The exception to be thrown.
   */
  public static void mockUpdateUserWithException(FirebaseAuth mockAuth,
                                                 FirebaseAuthException exceptionToThrow)
      throws FirebaseAuthException {
    doThrow(exceptionToThrow)
        .when(mockAuth).updateUser(any(UpdateRequest.class));
  }

  /**
   * Configures the mock to do nothing when {@link FirebaseAuth#deleteUser(String)} is called.
   *
   * @param mockAuth The FirebaseAuth mock instance to configure.
   */
  public static void mockDeleteUser(FirebaseAuth mockAuth)
      throws FirebaseAuthException {
    doNothing()
        .when(mockAuth).deleteUser(any(String.class));
  }

  /**
   * Configures the mock to throw the given {@link FirebaseAuthException} when {@link FirebaseAuth#deleteUser(String)} is called.
   *
   * @param mockAuth         The FirebaseAuth mock instance to configure.
   * @param exceptionToThrow The exception to be thrown.
   */
  public static void mockDeleteUserWithException(FirebaseAuth mockAuth,
                                                 FirebaseAuthException exceptionToThrow)
      throws FirebaseAuthException {
    doThrow(exceptionToThrow)
        .when(mockAuth).deleteUser(any(String.class));
  }

  // endregion API mock
}
