package dev.corusoft.eticketia.testing.integration.usecases.auth.signup;

import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.MAX_PASSWORD_LENGTH;
import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.MIN_PASSWORD_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord.CreateRequest;
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
import dev.corusoft.eticketia.testing.integration.mocks.auth.signup.FirebaseEmailPasswordSignUpUseCaseMock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Log4j2
public class FirebaseEmailPasswordSignUpUseCaseIT extends BaseIT {

  // region test variables
  private FirebaseExceptionHandler exceptionHandler;
  private FirebaseEmailPasswordSignUpUseCaseImpl useCase;
  private FirebaseAuth mockedFirebaseAuth;
  // endregion test variables

  // region Mocked services
  private FirebaseEmailPasswordSignUpUseCaseMock mockedUseCase;
  // endregion Mocked services


  // region Tests lifecycle

  @BeforeMethod
  void setupMocks() {
    // Mock the use case execution and replace it
    mockedUseCase = new FirebaseEmailPasswordSignUpUseCaseMock();
    mockedFirebaseAuth = mockedUseCase.getFirebaseAuthMock();
    exceptionHandler = new FirebaseExceptionHandler(Collections.emptyList());
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(mockedFirebaseAuth, exceptionHandler);
  }

  @AfterMethod
  void resetMocks() {
    mockedUseCase = null;
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
  void execute_signUpUseCase() throws Exception {
    // Arrange
    String uid = faker.internet().uuid();
    String nickname = faker.name().username();
    String email = faker.internet().safeEmailAddress(nickname);
    String password = faker.internet().password(
        MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, true, true, true
    );
    EmailPasswordSignUpInput useCaseInput = new EmailPasswordSignUpInput(email, password, nickname);

    // Configure mock for test
    this.mockedUseCase.mockEmailPasswordSignUpSuccess(uid, useCaseInput);

    // Act
    UserSignUpOutput output = useCase.execute(useCaseInput);

    // Assert
    assertThat(output).isNotNull();
    assertThat(output.signedUpUser().getUid()).isEqualTo(uid);
    assertThat(output.signedUpUser().getEmail()).isEqualTo(useCaseInput.email());
    assertThat(output.signedUpUser().getDisplayName()).isEqualTo(useCaseInput.nickname());
    assertThat(output.signedUpUser().getRegistrationDate()).isBeforeOrEqualTo(LocalDateTime.now());
    assertThat(output.signedUpUser().getLastUpdate()).isBeforeOrEqualTo(LocalDateTime.now());

    // Verify mock was used with the arranged values
    ArgumentCaptor<CreateRequest> captor = ArgumentCaptor.forClass(CreateRequest.class);
    verify(mockedUseCase.getFirebaseAuthMock(), atMostOnce()).createUser(captor.capture());
  }

  @Test(
      description = "Use case throws EmailAlreadyRegisteredException"
  )
  void execute_signUpUseCase_when_emailIsAlreadyRegistered() throws Exception {
    // Configure mock for test
    this.mockedUseCase.mockThrowEmailAlreadyRegisteredException();
    FirebaseAuthExceptionHandler existsEmailHandler = new EmailAlreadyExistsFirebaseAuthHandler();
    var customExceptionHandler = new FirebaseExceptionHandler(List.of(existsEmailHandler));
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(
        mockedFirebaseAuth, customExceptionHandler
    );
    customExceptionHandler.cacheExceptionHandlers();

    // Arrange
    String nickname = faker.name().username();
    String email = faker.internet().safeEmailAddress(nickname);
    String password = faker.internet().password(
        MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, true, true, true
    );
    EmailPasswordSignUpInput useCaseInput = new EmailPasswordSignUpInput(email, password, nickname);

    // Act
    assertThatThrownBy(
        () -> useCase.execute(useCaseInput)
    ).isInstanceOf(EmailAlreadyRegisteredException.class);

    // Verify mock was used
    verify(mockedUseCase.getFirebaseAuthMock(), atMostOnce()).createUser(any(CreateRequest.class));
  }

  @Test(
      description = "Use case throws UserAlreadyExistsException"
  )
  void execute_signUpUseCase_when_userAlreadyExists() throws Exception {
    // Arrange
    String uid = faker.internet().uuid();
    String nickname = faker.name().username();
    String email = faker.internet().safeEmailAddress(nickname);
    String password = faker.internet().password(
        MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, true, true, true
    );
    EmailPasswordSignUpInput useCaseInput = new EmailPasswordSignUpInput(email, password, nickname);

    // Configure mock for test
    this.mockedUseCase.mockThrowUserAlreadyExistException(uid, nickname);
    FirebaseAuthExceptionHandler authExceptionHandler = new UserAlreadyExistsFirebaseAuthHandler();
    FirebaseExceptionHandler firebaseExceptionHandler =
        new FirebaseExceptionHandler(List.of(authExceptionHandler));
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(
        mockedFirebaseAuth, firebaseExceptionHandler
    );
    firebaseExceptionHandler.cacheExceptionHandlers();


    // Act
    assertThatThrownBy(
        () -> useCase.execute(useCaseInput)
    ).isInstanceOf(UserAlreadyExistsException.class);

    // Verify mock was used
    verify(mockedUseCase.getFirebaseAuthMock(), atMostOnce()).createUser(any(CreateRequest.class));
  }

  @Test(
      description = "Use case throws UserAlreadyExistsException"
  )
  void execute_signUpUseCase_when_assigningRoles_then_userNotFound() throws Exception {
    // Arrange
    String uid = faker.internet().uuid();
    String nickname = faker.name().username();
    String email = faker.internet().safeEmailAddress(nickname);
    String password = faker.internet().password(
        MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, true, true, true
    );
    EmailPasswordSignUpInput useCaseInput = new EmailPasswordSignUpInput(email, password, nickname);

    // Configure mock for test
    this.mockedUseCase.mockThrowUserNotFoundException(uid, nickname);
    FirebaseAuthExceptionHandler authExceptionHandler = new UserNotFoundFirebaseAuthHandler();
    FirebaseExceptionHandler firebaseExceptionHandler =
        new FirebaseExceptionHandler(List.of(authExceptionHandler));
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(
        mockedFirebaseAuth, firebaseExceptionHandler
    );
    firebaseExceptionHandler.cacheExceptionHandlers();

    // Act
    assertThatThrownBy(
        () -> useCase.execute(useCaseInput)
    ).isInstanceOf(UserNotFoundException.class);

    // Verify mock was used
    verify(mockedUseCase.getFirebaseAuthMock(), atMostOnce()).createUser(any(CreateRequest.class));
  }

  @Test(
      description = "Use case cannot delete user on failure"
  )
  void when_signUpUseCase_failsDeletingUser_then_throwException() throws Exception {
    // Arrange
    String uid = faker.internet().uuid();
    String nickname = faker.name().username();
    String email = faker.internet().safeEmailAddress(nickname);
    String password = faker.internet().password(
        MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, true, true, true
    );
    EmailPasswordSignUpInput useCaseInput = new EmailPasswordSignUpInput(email, password, nickname);

    // Configure mock for test
    this.mockedUseCase.mockThrowExceptionWhenDeleteUser(uid, nickname);
    FirebaseAuthExceptionHandler authExceptionHandler = new UserNotFoundFirebaseAuthHandler();
    FirebaseExceptionHandler firebaseExceptionHandler =
        new FirebaseExceptionHandler(List.of(authExceptionHandler));
    useCase = new FirebaseEmailPasswordSignUpUseCaseImpl(
        mockedFirebaseAuth, firebaseExceptionHandler
    );
    firebaseExceptionHandler.cacheExceptionHandlers();

    // Act
    assertThatThrownBy(
        () -> useCase.execute(useCaseInput)
    ).isInstanceOf(UserNotFoundException.class);

    // Verify mock was used
    verify(mockedUseCase.getFirebaseAuthMock(), atMostOnce()).createUser(any(CreateRequest.class));
  }


}
