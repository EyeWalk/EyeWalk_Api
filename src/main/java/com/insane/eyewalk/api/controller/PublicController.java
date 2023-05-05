package com.insane.eyewalk.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class PublicController {

    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("EyeWalk API - online");
    }

}