package com.insane.eyewalk.api.security.auth;

import com.insane.eyewalk.api.user.Role;
import com.insane.eyewalk.api.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Method to register a new user
     * @param request is a RegisterRequest body type
     * @param principal is only needed to register superusers
     * @return Status code 200 OK, 403 Forbidden, 406 NotAcceptable if missing/invalid fields or 409 Conflict if email already exists on database
     */
     @PostMapping("/register")
     public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, Principal principal) {

        // VERIFY IF EMAIL ALREADY EXISTS
        if (userService.userEmailExists(request.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        // VERIFY IF REQUEST IS VALID
         if (!authenticationService.validateRegisterRequest(request, request.getPassword()))
             return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        // ADMIN OR EDITOR ROLES NEEDS TO BE A SUPERUSER TO BE ABLE TO REGISTER
        if (request.getRole() == Role.ADMIN || request.getRole() == Role.EDITOR) {
            if (principal != null && authenticationService.validatePermissionCreate(principal.getName()))
                return ResponseEntity.ok(authenticationService.register(request));
            else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else {
        // REGULAR USERS REGISTERING DOESNT NEED ANY PERMISSION
            request.setRole(Role.USER);
            return ResponseEntity.ok(authenticationService.register(request));
        }
     }

    /**
     * Method to authenticate a user already registered and retrieve the access token
     * @param authenticationRequest Authentication request body
     * @return Status code 200 OK if authenticated or else 403 Forbidden
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    /**
     * Method to refresh user's token using the refresh token
     * @param request Http Servlet Request containing on header the Authorization Bearer token
     * @param response Status Code 200 OK with new access token and refresh token on body, 401 no token present or 403 if invalid token
     * @throws IOException
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuthenticationResponse authenticationResponse = authenticationService.refreshToken(request, response);
        if (authenticationResponse != null)
            return ResponseEntity.ok(authenticationResponse);
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

}
