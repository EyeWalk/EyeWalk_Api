package com.insane.eyewalk.api.security.auth;

import com.insane.eyewalk.api.security.enums.Permission;
import com.insane.eyewalk.api.security.enums.Role;
import com.insane.eyewalk.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Method to register a new user
     * @param request is a RegisterRequest body type
     * @param principal is only needed to register superusers
     * @return Status code 200 OK, 403 Forbidden, 406 NotAcceptable if missing/invalid fields or 409 Conflict if email already exists on database
     */
     @Operation(
            summary = "Register a new user",
            description = "To register users with roles as Editor or Admin. The requester user must be an Administrator with create permission." +
                    "To register regular user no need to pass on body the role attribute. If the role informed is different than what is " +
                    "on the schema enum list, the USER role will be considered.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
     )
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
            if (authenticationService.validatePermission(principal, Permission.ADMIN_CREATE))
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
    @Operation(
            summary = "Authenticate user",
            description = "Authenticate an existing user to retrieve the access token and refresh token. All previous tokens will be set to expired!",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
    )
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
    @Operation(
            summary = "Refresh token",
            description = "To refresh a token is necessary to pass the refresh_token on the header as Authorization Bearer.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuthenticationResponse authenticationResponse = authenticationService.refreshToken(request);
        if (authenticationResponse != null)
            return ResponseEntity.ok(authenticationResponse);
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

}
