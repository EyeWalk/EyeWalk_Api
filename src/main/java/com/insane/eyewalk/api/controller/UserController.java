package com.insane.eyewalk.api.controller;

import com.insane.eyewalk.api.model.view.UserView;
import com.insane.eyewalk.api.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user-controller")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private ModelMapper mapper;

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
  @ResponseBody
  public UserView userDetails(Principal principal) {
      return mapper.map(userService.getUser(principal.getName()), UserView.class);
  }

    <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> mapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

}
