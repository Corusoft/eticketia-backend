package dev.corusoft.eticketia.server.controllers.auth.signup;

import static dev.corusoft.eticketia.infrastructure.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static dev.corusoft.eticketia.infrastructure.web.ApiPaths.AUTH_SIGNUP;

import dev.corusoft.eticketia.application.usecases.auth.signup.EmailPasswordSignUpInput;
import dev.corusoft.eticketia.application.usecases.auth.signup.EmailPasswordSignUpUseCase;
import dev.corusoft.eticketia.application.usecases.auth.signup.UserSignUpOutput;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.infrastructure.api.ApiResponse;
import dev.corusoft.eticketia.infrastructure.api.ApiResponseBuilder;
import dev.corusoft.eticketia.infrastructure.dtos.auth.AuthDtoConversor;
import dev.corusoft.eticketia.infrastructure.dtos.auth.inbound.FirebaseEmailPasswordSignUpInputDTO;
import dev.corusoft.eticketia.infrastructure.dtos.auth.outbound.AuthenticatedUserDTO;
import dev.corusoft.eticketia.infrastructure.security.EndpointSecurityConfigurer;
import dev.corusoft.eticketia.infrastructure.web.ApiPaths;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequestMapping(AUTH_SIGNUP)
@RestController
@Lazy
@Log4j2
@RequiredArgsConstructor
public class AuthSignUpController implements EndpointSecurityConfigurer {

  private final EmailPasswordSignUpUseCase signUpUseCase;

  @Override
  public void secureEndpoints(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(requests -> requests
        .requestMatchers(HttpMethod.POST, AUTH_SIGNUP).permitAll()
    );
  }

  @PostMapping(path = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ApiResponse<AuthenticatedUserDTO>> signUpWithEmailAndPassword(
      @Validated @RequestBody FirebaseEmailPasswordSignUpInputDTO body) throws DomainException {
    EmailPasswordSignUpInput input = new EmailPasswordSignUpInput(
        body.email(), body.password(), body.nickname()
    );
    UserSignUpOutput result = signUpUseCase.execute(input);

    // Build response
    AuthenticatedUserDTO authUserDto = AuthDtoConversor.toAuthenticatedUserDTO(
        result.token(), result.signedUpUser()
    );
    ApiResponse<AuthenticatedUserDTO> responseBody = ApiResponseBuilder.success(authUserDto);
    String newUserUri = ApiPaths.USER_BY_ID_FORMAT.formatted(USER_ID_ATTRIBUTE_NAME);
    URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
        .path(newUserUri)
        .buildAndExpand(result.signedUpUser().getId())
        .toUri();
    return ResponseEntity
        .created(resourceLocation)
        .contentType(MediaType.APPLICATION_JSON)
        .body(responseBody);
  }


}
