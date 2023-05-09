package com.insane.eyewalk.api.controller.web;

import com.insane.eyewalk.api.security.auth.AuthenticationResponse;
import com.insane.eyewalk.api.security.auth.AuthenticationService;
import com.insane.eyewalk.api.security.auth.RegisterRequest;
import com.insane.eyewalk.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/init")
@RequiredArgsConstructor
public class InitializerController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping
    public String initializeAPI() {
        if (userService.adminExists())
            return new ErrorController().error();
        else
            return "initializer/register";
    }

    @PostMapping
    public String registerAdmin(Model model, @Param("registerRequest") RegisterRequest registerRequest, @Param("passwordVerify") String passwordVerify) {
        if (!userService.adminExists()) {
            if (authenticationService.validateRegisterRequest(registerRequest, passwordVerify)) {
                AuthenticationResponse authenticationResponse = authenticationService.register(registerRequest);
                model.addAttribute("username", registerRequest.getName());
                model.addAttribute("key", authenticationResponse.getAccessToken());
            } else {
                model.addAttribute("error", "Invalid data. Please try again!");
            }
            return "initializer/register";
        }
        else return new ErrorController().error();
    }
}