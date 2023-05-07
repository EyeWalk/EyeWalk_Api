package com.insane.eyewalk.api.controller;

import com.insane.eyewalk.api.model.view.PlanView;
import com.insane.eyewalk.api.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/plan")
@Tag(name = "Plan")
public class PlanController {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PlanService planService;

    @Operation(
            summary = "Get Plan",
            description = "No permissions needed to retrieve a plan detail.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Not found / Invalid Plan Id", responseCode = "404")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PlanView> get(@PathVariable long id) {
        try {
            return ResponseEntity.ok(modelMapper.map(planService.getPlan(id), PlanView.class));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
