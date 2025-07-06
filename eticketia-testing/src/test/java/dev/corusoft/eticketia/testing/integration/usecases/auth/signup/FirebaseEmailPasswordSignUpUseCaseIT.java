package dev.corusoft.eticketia.testing.integration.usecases.auth.signup;

import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.MAX_PASSWORD_LENGTH;
import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.MIN_PASSWORD_LENGTH;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockCreateUser;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockCreateUserWithException;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockDeleteUserWithException;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockFirebaseAuth;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockUpdateUser;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMock.mockUpdateUserWithException;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMockDataFactory.createFirebaseAuthException;
import static dev.corusoft.eticketia.testing.integration.services.FirebaseAuthMockDataFactory.createMockedUserRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import dev.corusoft.eticketia.application.usecases.auth.signup.EmailPasswordSignUpInput;
import dev.corusoft.eticketia.application.usecases.auth.signup.UserSignUpOutput;
import dev.corusoft.eticketia.domain.exceptions.auth.EmailAlreadyRegisteredException;
import dev.corusoft.eticketia.domain.exceptions.auth.UserAlreadyExistsException;
import dev.corusoft.eticketia.domain.exceptions.auth.UserNotFoundException;
import dev.corusoft.eticketia.infrastructure.services.firebase.FirebaseExceptionHandler;
import dev.corusoft.eticketia.infrastructure.services.firebase.handlers.EmailAlreadyExistsFirebaseAuthHandler;
import dev.corusoft.eticketia.infrastructure.services.firebase.handlers.FirebaseAuthExceptionHandler;
import dev.corusoft.eticketia.infrastructure.services.firebase.handlers.UserAlreadyExistsFirebaseAuthHandler;
import dev.corusoft.eticketia.infrastructure.services.firebase.handlers.UserNotFoundFirebaseAuthHandler;
import dev.corusoft.eticketia.infrastructure.usecases.auth.signup.FirebaseEmailPasswordSignUpUseCaseImpl;
import dev.corusoft.eticketia.testing.integration.BaseIT;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Log4j2
public class FirebaseEmailPasswordSignUpUseCaseIT extends BaseIT {

  // region test variables
  private FirebaseExceptionHandler exceptionHandler;
  private FirebaseEmailPasswordSignUpUseCaseImpl useCase;
  private FirebaseAuth mockedFirebaseAuth;

  private String uid;
  private String email;
  private String password;
  private String nickname;
  private EmailPasswordSignUpInput useCaseInput;

  // endregion test variables

  // region Tests lifecycle

  @BeforeMethod
  void setupMocks() {
    // Mock the use case execution and replace it
    mockedFirebaseAuth = mockFirebaseAuth();
    exceptionHandler = new FirebaseExceptionHandler(Collections.emptyList());
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(mockedFirebaseAuth, exceptionHandler);
  }

  @BeforeMethod(dependsOnMethods = "setupMocks")
  void generateData() {
    this.uid = faker.internet().uuid();
    this.nickname = faker.name().username();
    this.email = faker.internet().safeEmailAddress(nickname);
    this.password = faker.internet().password(
        MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, true, true, true
    );
    this.useCaseInput = new EmailPasswordSignUpInput(email, password, nickname);
  }

  @AfterMethod
  void resetMocks() {
    useCase = null;
    exceptionHandler = new FirebaseExceptionHandler(Collections.emptyList());
  }

  // endregion Tests lifecycle


  @Test(
      description = "Check the name of the use case being tested"
  )
  void assertUseCaseTestedIsCorrect() {
    String expectedUseCaseClassname = useCase.getClass().getSimpleName();
    assertThat(useCase.getUseCaseName()).isEqualTo(expectedUseCaseClassname);
  }


  @Test(
      description = "Use case executes without errors"
  )
  void when_signUp_then_success() throws Exception {
    // Arrange
    UserRecord createUserMock = createMockedUserRecord(uid, null, null);
    UserRecord updatedUserMock = createMockedUserRecord(uid, email, nickname);
    mockCreateUser(mockedFirebaseAuth, createUserMock);
    mockUpdateUser(mockedFirebaseAuth, updatedUserMock);

    // Act
    UserSignUpOutput output = useCase.execute(useCaseInput);

    // Assert
    assertThat(output).isNotNull();
    assertThat(output.signedUpUser().getUid()).isEqualTo(uid);
    assertThat(output.signedUpUser().getEmail()).isEqualTo(useCaseInput.email());
    assertThat(output.signedUpUser().getDisplayName()).isEqualTo(useCaseInput.nickname());
    assertThat(output.signedUpUser().getRegistrationDate()).isBeforeOrEqualTo(LocalDateTime.now());
    assertThat(output.signedUpUser().getLastUpdate()).isBeforeOrEqualTo(LocalDateTime.now());

    // Verify mocks were used
    verify(mockedFirebaseAuth, atMostOnce()).createUser(any(CreateRequest.class));
    verify(mockedFirebaseAuth, atMostOnce()).updateUser(any(UpdateRequest.class));
  }

  @Test(
      description = "Use case throws EmailAlreadyRegisteredException"
  )
  void when_signUp_but_emailsIsAlreadyRegistered_then_fail() throws Exception {
    // Arrange
    FirebaseAuthExceptionHandler existsEmailHandler = new EmailAlreadyExistsFirebaseAuthHandler();
    var exceptionsHandler = new FirebaseExceptionHandler(List.of(existsEmailHandler));
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(mockedFirebaseAuth, exceptionsHandler);
    var mockedException = createFirebaseAuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
    mockCreateUserWithException(mockedFirebaseAuth, mockedException);

    // Act
    assertThatThrownBy(() -> useCase.execute(useCaseInput))
        .isInstanceOf(EmailAlreadyRegisteredException.class);

    // Verify mock was used
    verify(mockedFirebaseAuth, atMostOnce()).createUser(any(CreateRequest.class));
  }

  @Test(
      description = "Use case throws UserAlreadyExistsException"
  )
  void when_signUp_but_userAlreadyExists_then_fail() throws Exception {
    // Arrange
    FirebaseAuthExceptionHandler existsUserHandler = new UserAlreadyExistsFirebaseAuthHandler();
    var exceptionsHandler = new FirebaseExceptionHandler(List.of(existsUserHandler));
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(mockedFirebaseAuth, exceptionsHandler);
    var mockedException = createFirebaseAuthException(AuthErrorCode.UID_ALREADY_EXISTS);
    mockCreateUserWithException(mockedFirebaseAuth, mockedException);

    // Act
    assertThatThrownBy(() -> useCase.execute(useCaseInput))
        .isInstanceOf(UserAlreadyExistsException.class);

    // Verify mock was used
    verify(mockedFirebaseAuth, atMostOnce()).createUser(any(CreateRequest.class));
  }

  @Test(
      description = "Use case throws UserNotFoundException"
  )
  void when_signUp_but_cannotAssignRoles_then_fail() throws Exception {
    // Arrange
    FirebaseAuthExceptionHandler userNotFoundHandler = new UserNotFoundFirebaseAuthHandler();
    var exceptionsHandler = new FirebaseExceptionHandler(List.of(userNotFoundHandler));
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(mockedFirebaseAuth, exceptionsHandler);
    var mockedException = createFirebaseAuthException(AuthErrorCode.USER_NOT_FOUND);
    mockCreateUser(mockedFirebaseAuth, createMockedUserRecord(uid, null, null));
    mockUpdateUserWithException(mockedFirebaseAuth, mockedException);

    // Act
    assertThatThrownBy(() -> useCase.execute(useCaseInput))
        .isInstanceOf(UserNotFoundException.class);

    // Verify mock was used
    verify(mockedFirebaseAuth, atMostOnce()).createUser(any(CreateRequest.class));
  }

  @Test(
      description = "Use case cannot delete user on failure"
  )
  void when_signUp_failsDeletingUser_then_throwException() throws Exception {
    // Arrange
    FirebaseAuthExceptionHandler userNotFoundHandler = new UserNotFoundFirebaseAuthHandler();
    var exceptionsHandler = new FirebaseExceptionHandler(List.of(userNotFoundHandler));
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(mockedFirebaseAuth, exceptionsHandler);
    var mockedException = createFirebaseAuthException(AuthErrorCode.USER_NOT_FOUND);
    UserRecord mockedCreatedUser = createMockedUserRecord(uid, null, null);
    mockCreateUser(mockedFirebaseAuth, mockedCreatedUser);
    mockUpdateUserWithException(mockedFirebaseAuth, mockedException);
    mockDeleteUserWithException(mockedFirebaseAuth, mockedException);

    // Act
    assertThatThrownBy(() -> useCase.execute(useCaseInput))
        .isInstanceOf(UserNotFoundException.class);

    // Verify mock was used
    verify(mockedFirebaseAuth, atMostOnce()).createUser(any(CreateRequest.class));
  }


}
