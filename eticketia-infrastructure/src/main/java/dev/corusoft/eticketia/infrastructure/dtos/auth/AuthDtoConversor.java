package dev.corusoft.eticketia.infrastructure.dtos.auth;

import dev.corusoft.eticketia.domain.entities.users.User;
import dev.corusoft.eticketia.infrastructure.dtos.auth.outbound.AuthenticatedUserDTO;
import dev.corusoft.eticketia.infrastructure.dtos.user.UserConversor;
import dev.corusoft.eticketia.infrastructure.dtos.user.outbound.UserResumeDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthDtoConversor {
  // region to DTO

  public static AuthenticatedUserDTO toAuthenticatedUserDTO(String token, User entity) {
    UserResumeDTO userResumeDTO = UserConversor.toResumeUserDTO(entity);

    return new AuthenticatedUserDTO(token, userResumeDTO);
  }

  // endregion to DTO

  // region to entity

  // endregion to entity
}
