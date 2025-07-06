package dev.corusoft.eticketia.infrastructure.security.filters;

import static dev.corusoft.eticketia.infrastructure.security.SecurityConstants.PREFIX_BEARER_TOKEN;
import static dev.corusoft.eticketia.infrastructure.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static dev.corusoft.eticketia.infrastructure.security.SecurityConstants.USER_ROLES_CLAIM;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import dev.corusoft.eticketia.domain.entities.roles.RoleName;
import dev.corusoft.eticketia.infrastructure.security.CustomSecurityFilter;
import dev.corusoft.eticketia.infrastructure.services.firebase.FirebaseAuthenticatedUserDetails;
import dev.corusoft.eticketia.infrastructure.web.ApiPaths;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to validate and authenticate requests using Firebase Authentication.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class FirebaseTokenAuthenticationFilter extends OncePerRequestFilter
    implements CustomSecurityFilter {
  private static final List<String> PUBLIC_ENDPOINTS = List.of(
      ApiPaths.AUTH_SIGNUP
  );

  private final FirebaseAuth firebaseAuth;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res,
                                  @NonNull FilterChain chain) throws ServletException, IOException {
    if (!canApplyFilter(req)) {
      chain.doFilter(req, res);
      return;
    }

    try {
      String authorizationHeaderTokenValue = extractTokenFromRequest(req);
      FirebaseToken decodedToken = firebaseAuth.verifyIdToken(authorizationHeaderTokenValue, true);
      UsernamePasswordAuthenticationToken authToken = buildAuthenticationToken(decodedToken, req);
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } catch (FirebaseAuthException e) {
      log.error("Could not verify Firebase token");
      throw new ServletException(e);
    }


    // Continue filtering requests
    chain.doFilter(req, res);
  }

  @Override
  public boolean canApplyFilter(HttpServletRequest req) {
    try {
      return !isPublicEndpoint(req);
    } catch (Exception e) {
      log.debug("Can not apply filter {} : {}", getClass().getName(), e.getMessage());
      return false;
    }
  }

  private boolean isPublicEndpoint(HttpServletRequest req) {
    String path = req.getServletPath();

    return PUBLIC_ENDPOINTS.contains(path);
  }

  private UsernamePasswordAuthenticationToken buildAuthenticationToken(FirebaseToken token,
                                                                       HttpServletRequest req) {
    // Add user related values from token into security context
    req.setAttribute(USER_ID_ATTRIBUTE_NAME, token.getUid());
    FirebaseAuthenticatedUserDetails userDetails = new FirebaseAuthenticatedUserDetails(token);

    // Set user roles
    Set<GrantedAuthority> authorities = createAuthorities(token);

    return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
  }


  private String extractTokenFromRequest(HttpServletRequest req) throws BadCredentialsException {
    // Check response contains Authorization header
    String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.isBlank(authHeader)) {
      String message =
          "Request has no '%s' header or is empty".formatted(HttpHeaders.AUTHORIZATION);
      throw new BadCredentialsException(message);
    }

    // Remove "Bearer " from header
    String token = authHeader.replace(PREFIX_BEARER_TOKEN, "");
    if (StringUtils.isBlank(token)) {
      log.debug("Could not extract JWT token from request or it is malformed");
      throw new BadCredentialsException("Non existent or malformed token in request");
    }

    return token;
  }

  private Set<GrantedAuthority> createAuthorities(FirebaseToken token) {
    Set<RoleName> roles = extractRolesFromToken(token);
    if (roles.isEmpty()) {
      return Collections.emptySet();
    }

    log.debug("Registering granted authorities for user '{}'", token.getName());
    Set<GrantedAuthority> authorities = HashSet.newHashSet(roles.size());
    roles.stream()
        .map(RoleName::getName)
        .map(SimpleGrantedAuthority::new)
        .forEach(authorities::add);

    log.debug("Granted authorities registered for user '{}'", token.getName());
    return authorities;
  }

  @SuppressWarnings("unchecked")
  private Set<RoleName> extractRolesFromToken(FirebaseToken token) {
    Map<String, Object> claims = token.getClaims();
    if (claims == null) {
      log.debug("User '{}' has no roles assigned", token.getName());
      return Collections.emptySet();
    }
    Set<String> rolesClaim = (Set<String>) claims.get(USER_ROLES_CLAIM);

    return rolesClaim.stream()
        .map(RoleName::valueOf)
        .collect(Collectors.toUnmodifiableSet());
  }
}
