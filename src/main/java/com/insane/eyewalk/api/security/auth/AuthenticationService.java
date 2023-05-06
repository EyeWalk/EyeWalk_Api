package com.insane.eyewalk.api.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insane.eyewalk.api.security.config.JwtService;
import com.insane.eyewalk.api.security.token.Token;
import com.insane.eyewalk.api.security.token.TokenRepository;
import com.insane.eyewalk.api.security.token.TokenType;
import com.insane.eyewalk.api.user.Permission;
import com.insane.eyewalk.api.user.User;
import com.insane.eyewalk.api.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.insane.eyewalk.api.user.Permission.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .created(LocalDate.now())
        .lastVisit(LocalDate.now())
        .active(true)
        .role(request.getRole())
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    user.setLastVisit(LocalDate.now());
    tokenRepository.save(token);
    userRepository.save(user);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  /**
   * This method will validate a register request. To be able to register an account with Administrator permissions
   * or Manager permissions the user must have an Admin Role. Otherwise, to register any other type of account
   * any active user will validate the request.
   * @param username principal user
   * @param registerRequest account register request
   * @return boolean true if valid to proceed
   * @throws UsernameNotFoundException if no username found on repository
   */
  public boolean validateRegisterRequest(String username, RegisterRequest registerRequest) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    Set<Permission> userPermissions = user.getRole().getPermissions();
    Set<Permission> requestPermissions = registerRequest.getRole().getPermissions();
    if (
            requestPermissions.contains(ADMIN_READ) ||
            requestPermissions.contains(ADMIN_UPDATE) ||
            requestPermissions.contains(ADMIN_DELETE) ||
            requestPermissions.contains(ADMIN_CREATE) ||
            requestPermissions.contains(EDITOR_READ) ||
            requestPermissions.contains(EDITOR_UPDATE) ||
            requestPermissions.contains(EDITOR_DELETE) ||
            requestPermissions.contains(EDITOR_CREATE)
    ) {
      // NEEDS TO BE AN ADMIN TO BE ABLE TO REGISTER AN ACCOUNT WITH THESE PERMISSIONS
      return (userPermissions.contains(Permission.ADMIN_CREATE) && user.isActive());
    }
    return (userPermissions.contains(Permission.USER_CREATE) && user.isActive());
  }

}
