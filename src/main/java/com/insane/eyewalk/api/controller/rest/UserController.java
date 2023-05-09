package com.insane.eyewalk.api.controller.rest;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.model.view.UserView;
import com.insane.eyewalk.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final ModelMapperList modelMapping;

    @Operation(
            summary = "List all users",
            description = "To get user's list the user needs to have editor read permission.",
            responses = {
                    @ApiResponse(description = "Success",responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token",responseCode = "403")
            }
    )
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('editor:read')")
    @ResponseBody
    public ResponseEntity<List<UserView>> listUsers(Principal principal) {
        try {
            return ResponseEntity.ok(modelMapping.mapList(userService.getAll(principal), UserView.class));
        } catch (IllegalAccessError e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

  @Operation(
          summary = "View user details",
          description = "To get user details the request must contain the user token.",
          responses = {
                  @ApiResponse(description = "Success",responseCode = "200"),
                  @ApiResponse(description = "Unauthorized / Invalid Token",responseCode = "403")
          }
  )
  @GetMapping
  @ResponseBody
  public UserView userDetails(Principal principal) {
      return modelMapping.map(userService.getUser(principal.getName()), UserView.class);
  }

}
