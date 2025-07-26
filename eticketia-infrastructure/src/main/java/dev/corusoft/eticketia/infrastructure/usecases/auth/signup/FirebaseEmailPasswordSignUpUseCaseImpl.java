package dev.corusoft.eticketia.infrastructure.usecases.auth.signup;

import static dev.corusoft.eticketia.application.usecases.auth.AuthConstraints.DEFAULT_NEW_USER_ROLE;
import static dev.corusoft.eticketia.infrastructure.security.SecurityConstants.USER_ROLES_CLAIM;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import dev.corusoft.eticketia.application.usecases.auth.signup.EmailPasswordSignUpInput;
import dev.corusoft.eticketia.application.usecases.auth.signup.EmailPasswordSignUpUseCase;
import dev.corusoft.eticketia.application.usecases.auth.signup.UserSignUpOutput;
import dev.corusoft.eticketia.domain.entities.roles.RoleName;
import dev.corusoft.eticketia.domain.entities.users.User;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.infrastructure.services.firebase.FirebaseExceptionHandler;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link EmailPasswordSignUpUseCase} using Firebase Authentication.
 */
@Service
@Lazy
@RequiredArgsConstructor
@Log4j2
public final class FirebaseEmailPasswordSignUpUseCaseImpl implements EmailPasswordSignUpUseCase {

  private final FirebaseAuth firebaseAuth;
  private final FirebaseExceptionHandler firebaseExceptionHandler;

  @Override
  public String getUseCaseName() {
    return getClass().getSimpleName();
  }

  /**
   * Registers a new user in the application.
   *
   * @param input Parameters to register the new user
   * @return A new registered user
   * @throws DomainException An error ocurred creating the new user
   */
  @Override
  public UserSignUpOutput execute(EmailPasswordSignUpInput input) throws DomainException {
    UserRecord createdUser = doCreateUser(input);
    try {
      createdUser = doAssignRoles(createdUser);
      log.debug("Registered new user '{}'", createdUser.getDisplayName());
    } catch (DomainException e) {
      try {
        firebaseAuth.deleteUser(createdUser.getUid());
        log.debug(
            "Deleted user {} because of failure assigning roles",
            createdUser.getDisplayName()
        );
      } catch (FirebaseAuthException firebaseException) {
        DomainException ex = firebaseExceptionHandler.toDomainException(firebaseException, input);
        log.error("Error deleting new user '{}'", input.nickname(), ex);
      }

      throw e;
    }

    return buildOutput(createdUser);
  }

  public UserRecord doCreateUser(EmailPasswordSignUpInput input) throws DomainException {
    CreateRequest createRequest = new CreateRequest()
        .setEmail(input.email())
        .setPassword(input.password())
        .setDisplayName(input.nickname());

    try {
      return firebaseAuth.createUser(createRequest);
    } catch (FirebaseAuthException e) {
      DomainException ex = firebaseExceptionHandler.toDomainException(e, input);
      log.warn("Error creating new user: {}", input.nickname(), ex);
      throw ex;
    }
  }

  // region auxiliar methods

  public UserRecord doAssignRoles(UserRecord user) throws DomainException {
    Map<String, Object> customClaims = buildNewUserClaims();
    UpdateRequest updateRequest = new UpdateRequest(user.getUid())
        .setCustomClaims(customClaims);

    try {
      return firebaseAuth.updateUser(updateRequest);
    } catch (FirebaseAuthException e) {
      DomainException ex = firebaseExceptionHandler.toDomainException(e);
      log.warn("Error assigning default role for user '{}'", user.getDisplayName());
      throw ex;
    }
  }

  public static Map<String, Object> buildNewUserClaims() {
    Map<String, Object> customClaims = new TreeMap<>();

    customClaims.put(USER_ROLES_CLAIM, DEFAULT_NEW_USER_ROLE.getName());

    return customClaims;
  }

  private String generateToken(UserRecord user) throws DomainException {
    try {
      return firebaseAuth.createCustomToken(user.getUid());
    } catch (FirebaseAuthException e) {
      DomainException ex = firebaseExceptionHandler.toDomainException(e);
      log.warn("Error generating token for user '{}'", user.getDisplayName());
      throw ex;
    }
  }

  @Override
  public UserSignUpOutput buildOutput(Object result) {
    UserRecord userRecord = (UserRecord) result;

    long creationTimestamp = userRecord.getUserMetadata().getCreationTimestamp();
    LocalDateTime creationDate = Instant.ofEpochMilli(creationTimestamp)
        .atZone(ZoneOffset.UTC)
        .toLocalDateTime();
    String roleClaim = String.valueOf(userRecord.getCustomClaims().get(USER_ROLES_CLAIM));
    RoleName role = (roleClaim != null) ? RoleName.valueOf(roleClaim) : DEFAULT_NEW_USER_ROLE;

    User user = User.builder()
        .id(userRecord.getUid())
        .email(userRecord.getEmail())
        .displayName(userRecord.getDisplayName())
        .registrationDate(creationDate)
        .lastUpdate(creationDate)
        .role(role)
        .build();

    String userToken;
    try {
      userToken = generateToken(userRecord);
    } catch (DomainException e) {
      throw new RuntimeException(e);
    }

    return new UserSignUpOutput(user, userToken);
  }

  // endregion auxiliar methods

}
