package com.insane.eyewalk.api.security.auth;

import com.insane.eyewalk.api.security.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String name;
  private String email;
  private String password;
  private Role role;
}
