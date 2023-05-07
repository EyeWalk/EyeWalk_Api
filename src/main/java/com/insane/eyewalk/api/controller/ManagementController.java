package com.insane.eyewalk.api.controller;

import com.insane.eyewalk.api.model.Plan;
import com.insane.eyewalk.api.model.input.PlanInput;
import com.insane.eyewalk.api.model.view.PlanView;
import com.insane.eyewalk.api.security.auth.AuthenticationResponse;
import com.insane.eyewalk.api.service.PlanService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/management")
@Tag(name = "Management")
public class ManagementController {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PlanService planService;

    @Operation(
            summary = "Create Plan",
            description = "To be able to create new plans, user must have editor permission.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Unauthorized / Invalid Token", responseCode = "403")
            }
    )
    @PostMapping("/plan")
    @PreAuthorize("hasAuthority('editor:create')")
    @ResponseBody
    public PlanView createPlan(PlanInput planInput, Principal principal) {
        return modelMapper.map(planService.createPlan(planInput, principal), PlanView.class);
    }

    @PutMapping
    @Hidden
    public String put() {
        return "PUT:: management controller";
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
    public ResponseEntity<HttpStatus> delete(@PathVariable long id, Principal principal) {
        return new ResponseEntity<>(planService.deletePlan(id, principal));
    }

    <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

}
