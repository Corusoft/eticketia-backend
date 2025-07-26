package dev.corusoft.eticketia.domain.entities.users;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import dev.corusoft.eticketia.domain.entities.roles.RoleName;
import dev.corusoft.eticketia.domain.repositories.Identifiable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Identifiable {

  @EqualsAndHashCode.Include
  @Accessors(chain = false)
  @DocumentId
  private String id;

  private String email;
  private String displayName;
  private LocalDateTime registrationDate;
  @ServerTimestamp
  private LocalDateTime lastUpdate;
  private RoleName role;

}
