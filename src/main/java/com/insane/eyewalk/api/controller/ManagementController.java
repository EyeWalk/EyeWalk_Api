package com.insane.eyewalk.api.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/management")
@Tag(name = "Management")
@Hidden
public class ManagementController {


    @Operation(
            description = "Get endpoint for manager user role",
            summary = "Management endpoint",
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
    @Hidden
    public String get() {
        return "GET:: management controller";
    }
    @PostMapping
    @Hidden
    public String post() {
        return "POST:: management controller";
    }
    @PutMapping
    @Hidden
    public String put() {
        return "PUT:: management controller";
    }
    @DeleteMapping
    @Hidden
    public String delete() {
        return "DELETE:: management controller";
    }
}
