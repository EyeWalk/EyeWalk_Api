package com.insane.eyewalk.api.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PublicController {

    @GetMapping
    public String home() {
        return "home/home";
    }

}
