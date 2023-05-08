package com.insane.eyewalk.api.controller;

import com.insane.eyewalk.api.model.input.PlanInput;
import com.insane.eyewalk.api.model.view.PlanView;
import com.insane.eyewalk.api.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/management")
@RequiredArgsConstructor
@Tag(name = "Management")
public class ManagementController {

    private final ModelMapper modelMapper;
    private final PlanService planService;

    @Operation(
            summary = "Create Plan",
            description = "To be able to create new plans, user must have editor create permission.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
    )
    @PostMapping("/plan")
    @PreAuthorize("hasAuthority('editor:create')")
    @ResponseBody
    public PlanView createPlan(@RequestBody PlanInput planInput, Principal principal) {
        return modelMapper.map(planService.createPlan(planInput, principal), PlanView.class);
    }

    @Operation(
            summary = "Update Plan",
            description = "To be able to update plans, user must have editor update permission.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
    )
    @PutMapping("/plan/{id}")
    @PreAuthorize("hasAuthority('editor:update')")
    @ResponseBody
    public ResponseEntity<PlanView> updatePlan(@PathVariable long id, @RequestBody PlanInput planInput, Principal principal) {
        try {
            return ResponseEntity.ok(modelMapper.map(planService.updatePlan(id, planInput, principal), PlanView.class));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalAccessError e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @Operation(
            summary = "Delete Plan",
            description = "To be able to delete a plan, user must have administrator permission.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
    )
    @DeleteMapping("/plan/{id}")
    public ResponseEntity<HttpStatus> deletePlan(@PathVariable long id, Principal principal) {
        return new ResponseEntity<>(planService.deletePlan(id, principal));
    }

}
