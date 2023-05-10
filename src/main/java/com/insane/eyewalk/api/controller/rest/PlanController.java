package com.insane.eyewalk.api.controller.rest;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.model.input.PlanInput;
import com.insane.eyewalk.api.model.view.PlanView;
import com.insane.eyewalk.api.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
@Tag(name = "Plan")
public class PlanController {

    private final ModelMapperList modelMapping;
    private final PlanService planService;

    @Operation(
            summary = "Create Plan",
            description = "To be able to create new plans, user must have editor create permission.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('editor:create')")
    @ResponseBody
    public ResponseEntity<PlanView> createPlan(@RequestBody PlanInput planInput, Principal principal) {
        try {
            return ResponseEntity.ok(modelMapping.map(planService.createPlan(planInput, principal), PlanView.class));
        } catch (IllegalAccessError e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @Operation(
            summary = "Update Plan",
            description = "To be able to update plans, user must have editor update permission.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('editor:update')")
    @ResponseBody
    public ResponseEntity<PlanView> updatePlan(@PathVariable long id, @RequestBody PlanInput planInput, Principal principal) {
        try {
            return ResponseEntity.ok(modelMapping.map(planService.updatePlan(id, planInput, principal), PlanView.class));
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
                    @ApiResponse(description = "Not Found", responseCode = "404"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
    )
    @DeleteMapping("/{id}")
    public HttpStatus deletePlan(@PathVariable long id, Principal principal) {
        try {
            planService.deletePlan(id, principal);
            return HttpStatus.OK;
        } catch (NoSuchElementException e) {
            return HttpStatus.NOT_FOUND;
        } catch (IllegalAccessError ignored) {
            return HttpStatus.FORBIDDEN;
        }
    }

    @Operation(
            summary = "List all Plans available",
            description = "No permissions needed.",
            responses = {@ApiResponse(description = "Success", responseCode = "200")}
    )
    @GetMapping
    public ResponseEntity<List<PlanView>> getPlanList() {
        return ResponseEntity.ok(modelMapping.mapList(planService.getPlanList(), PlanView.class));
    }

    @Operation(
            summary = "Get Plan",
            description = "No permissions needed to retrieve a plan detail.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Not found / Invalid Plan Id", responseCode = "404")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PlanView> getPlan(@PathVariable long id) {
        try {
            return ResponseEntity.ok(modelMapping.map(planService.getPlan(id), PlanView.class));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}