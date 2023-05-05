package com.insane.eyewalk.api.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-controller")
public class UserController {

  @Operation(
          description = "Get endpoint for user role",
          summary = "User endpoint",
          responses = {
                  @ApiResponse(
                          description = "Success",
                          responseCode = "200"
                  ),
                  @ApiResponse(
                          description = "Unauthorized / Invalid Token",
                          responseCode = "403"
                  )
          }

  )
  @GetMapping
  public ResponseEntity<String> sayHello() {
    return ResponseEntity.ok("This is the User secured endpoint");
  }

}
