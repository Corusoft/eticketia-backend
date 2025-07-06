package dev.corusoft.eticketia.infrastructure.dtos.user;

import dev.corusoft.eticketia.domain.entities.users.User;
import dev.corusoft.eticketia.infrastructure.dtos.user.outbound.UserResumeDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConversor {
  // region to DTO

  public static UserResumeDTO toResumeUserDTO(User entity) {
    return new UserResumeDTO(
        entity.getUid(),
        entity.getEmail(),
        entity.getDisplayName(),
        entity.getRegistrationDate()
    );
  }

  // endregion to DTO

  // region to entity

  // endregion to entity
}
