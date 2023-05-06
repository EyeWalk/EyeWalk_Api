package com.insane.eyewalk.api.controller;

import com.insane.eyewalk.api.security.auth.AuthenticationResponse;
import com.insane.eyewalk.api.security.auth.AuthenticationService;
import com.insane.eyewalk.api.security.auth.RegisterRequest;
import com.insane.eyewalk.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/init")
public class InitializerController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public String initializeAPI() {
        if (userService.existsAdmin())
            return new ErrorController().error();
        else
            return "initializer/register";
    }

    @PostMapping
    public String registerAdmin(Model model, @Param("registerRequest") RegisterRequest registerRequest, @Param("passwordVerify") String passwordVerify) {
        if (!userService.existsAdmin()) {
            if (userService.validateCredential(registerRequest, passwordVerify)) {
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