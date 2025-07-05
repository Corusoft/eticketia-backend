package dev.corusoft.eticketia.testing.integration.mocks.auth.signup;

import static dev.corusoft.eticketia.infrastructure.security.SecurityConstants.USER_ROLES_CLAIM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserMetadata;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import dev.corusoft.eticketia.application.usecases.auth.signup.EmailPasswordSignUpInput;
import dev.corusoft.eticketia.domain.entities.roles.RoleName;
import dev.corusoft.eticketia.domain.exceptions.auth.EmailAlreadyRegisteredException;
import dev.corusoft.eticketia.domain.exceptions.auth.UserAlreadyExistsException;
import dev.corusoft.eticketia.domain.exceptions.auth.UserNotFoundException;
import dev.corusoft.eticketia.infrastructure.usecases.auth.signup.FirebaseEmailPasswordSignUpUseCaseImpl;
import dev.corusoft.eticketia.testing.utils.TimeUtils;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Mocking implementation for {@link FirebaseEmailPasswordSignUpUseCaseImpl} use case.
 */
@Log4j2
@Getter
public class FirebaseEmailPasswordSignUpUseCaseMock {
  private final FirebaseAuth firebaseAuthMock;

  /**
   * Builds the mock instance for {@link FirebaseAuth}.
   */
  public FirebaseEmailPasswordSignUpUseCaseMock() {
    this.firebaseAuthMock = mock(FirebaseAuth.class);
  }

  /**
   * Configure the mock to sucessfuly execute the {@link FirebaseEmailPasswordSignUpUseCaseImpl} use case.
   *
   * @param uid   The UID to be returned when the user is created
   * @param input The input parameters for the use case.
   */
  public void mockEmailPasswordSignUpSuccess(String uid, EmailPasswordSignUpInput input)
      throws FirebaseAuthException {
    // Mock user signup
    UserRecord createdUserMock = mock(UserRecord.class);
    when(createdUserMock.getUid()).thenReturn(uid);
    when(
        firebaseAuthMock.createUser(any(CreateRequest.class))
    ).thenReturn(createdUserMock);

    // Build mock return data
    Long currentTimeMillis = TimeUtils.toUtcMilliseconds(LocalDateTime.now());
    UserRecord updatedUserMock = mock(UserRecord.class);
    UserMetadata userMetadataMock = mock(UserMetadata.class);
    when(updatedUserMock.getUid()).thenReturn(uid);
    when(updatedUserMock.getEmail()).thenReturn(input.email());
    when(updatedUserMock.getDisplayName()).thenReturn(input.nickname());
    when(updatedUserMock.getUserMetadata()).thenReturn(userMetadataMock);
    when(userMetadataMock.getCreationTimestamp()).thenReturn(currentTimeMillis);
    // Mock role assignment
    Map<String, Object> userRolesClaim = Map.of(USER_ROLES_CLAIM, RoleName.BASIC.name());
    when(updatedUserMock.getCustomClaims()).thenReturn(userRolesClaim);

    when(
        firebaseAuthMock.updateUser(any(UpdateRequest.class))
    ).thenReturn(updatedUserMock);
  }

  /**
   * Configure the mock to throw a {@link EmailAlreadyRegisteredException}.
   */
  public void mockThrowEmailAlreadyRegisteredException() throws FirebaseAuthException {
    FirebaseAuthException firebaseException = mock(FirebaseAuthException.class);

    when(
        firebaseException.getAuthErrorCode()
    ).thenReturn(AuthErrorCode.EMAIL_ALREADY_EXISTS);
    when(
        firebaseAuthMock.createUser(any(CreateRequest.class))
    ).thenThrow(firebaseException);
  }

  /**
   * Configure the mock to throw a {@link UserAlreadyExistsException}.
   */
  public void mockThrowUserAlreadyExistException(String uid, String nickname)
      throws FirebaseAuthException {

    // Mock user signup correct
    UserRecord createdUserMock = mock(UserRecord.class);
    when(createdUserMock.getUid()).thenReturn(uid);
    when(createdUserMock.getDisplayName()).thenReturn(nickname);
    when(
        firebaseAuthMock.createUser(any(CreateRequest.class))
    ).thenReturn(createdUserMock);

    // Mock assign roles throws exception
    FirebaseAuthException firebaseException = mock(FirebaseAuthException.class);
    when(
        firebaseException.getAuthErrorCode()
    ).thenReturn(AuthErrorCode.UID_ALREADY_EXISTS);
    when(
        firebaseAuthMock.updateUser(any(UpdateRequest.class))
    ).thenThrow(firebaseException);

    // Avoid executing deleteUser in firebase
    doNothing().when(firebaseAuthMock).deleteUser(any(String.class));
  }

  /**
   * Configure the mock to throw a {@link UserNotFoundException}.
   *
   * @param uid The UID to be returned when the user is created
   */
  public void mockThrowUserNotFoundException(String uid, String nickname)
      throws FirebaseAuthException {
    // Mock user signup
    UserRecord createdUserMock = mock(UserRecord.class);
    when(createdUserMock.getUid()).thenReturn(uid);
    when(createdUserMock.getDisplayName()).thenReturn(nickname);
    when(
        firebaseAuthMock.createUser(any(CreateRequest.class))
    ).thenReturn(createdUserMock);

    FirebaseAuthException firebaseException = mock(FirebaseAuthException.class);
    when(firebaseException.getAuthErrorCode()).thenReturn(AuthErrorCode.USER_NOT_FOUND);
    when(
        firebaseAuthMock.updateUser(any(UpdateRequest.class))
    ).thenThrow(firebaseException);

    // Avoid executing deleteUser in firebase
    doNothing().when(firebaseAuthMock).deleteUser(any(String.class));
  }

  public void mockThrowExceptionWhenDeleteUser(String uid, String nickname)
      throws FirebaseAuthException {
    // Mock user signup
    UserRecord createdUserMock = mock(UserRecord.class);
    when(createdUserMock.getUid()).thenReturn(uid);
    when(createdUserMock.getDisplayName()).thenReturn(nickname);
    when(
        firebaseAuthMock.createUser(any(CreateRequest.class))
    ).thenReturn(createdUserMock);

    // Mock assign roles throws exception
    FirebaseAuthException updateUserException = mock(FirebaseAuthException.class);
    when(updateUserException.getAuthErrorCode()).thenReturn(AuthErrorCode.USER_NOT_FOUND);
    when(
        firebaseAuthMock.updateUser(any(UpdateRequest.class))
    ).thenThrow(updateUserException);

    // Mock throw exception when deleting user
    FirebaseAuthException deleteUserException = mock(FirebaseAuthException.class);
    when(deleteUserException.getAuthErrorCode()).thenReturn(AuthErrorCode.USER_NOT_FOUND);
    doThrow(deleteUserException).when(firebaseAuthMock).deleteUser(any(String.class));
  }
}
