package dev.corusoft.eticketia.domain.entities.users;

import dev.corusoft.eticketia.domain.entities.roles.RoleName;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class User {
  @EqualsAndHashCode.Include
  @Id
  @Column(name = "uid", nullable = false, unique = true)
  private String uid;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "registered_at", nullable = false, updatable = false)
  private LocalDateTime registrationDate;

  @Column(name = "last_updated", nullable = false)
  private LocalDateTime lastUpdate;

  @Column(name = "role", nullable = false)
  private RoleName role;


  @PrePersist
  @PreUpdate
  protected void onUpdate() {
    this.lastUpdate = LocalDateTime.now();
  }

}
