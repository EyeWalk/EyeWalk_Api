package com.insane.eyewalk.api.controller.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/management")
@Tag(name = "Management")
@CrossOrigin
@Hidden
public class ManagementController {

    @GetMapping
    @PreAuthorize("hasAuthority('editor:read')")
    public String get() {
        return "GET:: editor controller";
    }
    @PostMapping
    @PreAuthorize("hasAuthority('editor:create')")
    public String post() {
        return "POST:: editor controller";
    }
    @PutMapping
    @PreAuthorize("hasAuthority('editor:update')")
    public String put() {
        return "PUT:: editor controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority('editor:delete')")
    public String delete() {
        return "DELETE:: editor controller";
    }

}
