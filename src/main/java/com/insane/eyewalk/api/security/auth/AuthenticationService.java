package com.insane.eyewalk.api.security.auth;

import com.insane.eyewalk.api.security.config.JwtService;
import com.insane.eyewalk.api.security.token.Token;
import com.insane.eyewalk.api.security.token.TokenRepository;
import com.insane.eyewalk.api.security.token.TokenType;
import com.insane.eyewalk.api.user.Permission;
import com.insane.eyewalk.api.user.User;
import com.insane.eyewalk.api.user.UserRepository;
import com.insane.eyewalk.api.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Method to register a new user
     * @param request Register Request body required
     * @return with Authentication Response containing access token and refresh token on body
     */
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
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    /**
     * Method to authenticate a user already registered
     * @param request Authentication Request body required containing user's email and password
     * @return with Authentication Response containing access token and refresh token on body
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    /**
     * Method to persist a user's token on database
     * @param user object User
     * @param jwtToken a String containing the user's token to be persisted
     */
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

    /**
     * Method to revoke all valid user's token on database
     * @param user object User
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Method to refresh an expired token using the refresh token if valid
     * @param request Http Servlet Request containing the Authorization Header Bearer with the refresh token
     * @param response Authentication Response containing the access token and a new refresh token
     * @return Http Status 200 OK containing access token and refresh token on body, 401 Unauthorized if no authorization is supplied or 403 Forbidden if refresh token is invalid
     * @throws NoSuchElementException if no user is found
     */
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws NoSuchElementException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            }
        }
        return null;
    }

    /**
     * Validates if the user is active and if it has permission to access the service requested.
     * The permission should be already validated on SecurityConfiguration but if any endpoint get exposed
     * Here the validation would take effect otherwise it will check if the user is active or not.
     * @param principal user's identification email or principal
     * @return boolean true if user is active and has permission to go through
     * @throws UsernameNotFoundException if user was not found on repository
     */
    public boolean validatePermission(Principal principal, Permission permission) throws UsernameNotFoundException {
        if (principal == null || permission == null) return false;
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(("User not found!")));
        return (user.isActive() && user.getRole().getPermissions().contains(permission));
    }

    /**
     * Method to verify if Register Request follow the rules such as password length and non-nullable fields
     * @param registerRequest Register request to be verified
     * @param passwordVerify Password to match
     * @return boolean true if validated
     */
    public boolean validateRegisterRequest(RegisterRequest registerRequest, String passwordVerify) {
        return (
            (registerRequest.getName() != null && !registerRequest.getName().isEmpty()) &&
            (registerRequest.getEmail() !=null && !registerRequest.getEmail().isEmpty()) &&
            (!userService.userEmailExists(registerRequest.getEmail())) &&
            (registerRequest.getPassword() != null && !registerRequest.getPassword().isEmpty()) &&
            (registerRequest.getPassword().length() >= 5) &&
            (registerRequest.getPassword().equals(passwordVerify))
        );
    }

}
