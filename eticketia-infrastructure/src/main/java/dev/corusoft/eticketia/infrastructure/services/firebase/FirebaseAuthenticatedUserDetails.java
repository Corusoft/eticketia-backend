package dev.corusoft.eticketia.infrastructure.services.firebase;

import com.google.firebase.auth.FirebaseToken;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class FirebaseAuthenticatedUserDetails implements UserDetails {
  private String uid;
  private String name;
  private String email;
  private String picture;
  private transient Map<String, Object> claims;

  public FirebaseAuthenticatedUserDetails(FirebaseToken token) {
    this.uid = token.getUid();
    this.name = token.getName();
    this.email = token.getEmail();
    this.picture = token.getPicture();
    this.claims = token.getClaims();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return (email != null) ? email : uid;
  }

}
