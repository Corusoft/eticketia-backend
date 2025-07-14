package dev.corusoft.eticketia.domain.entities.users;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import dev.corusoft.eticketia.domain.entities.roles.RoleName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @DocumentId
  @EqualsAndHashCode.Include
  private String uid;
  private String email;
  private String displayName;
  private LocalDateTime registrationDate;
  @ServerTimestamp
  private LocalDateTime lastUpdate;
  private RoleName role;

}
