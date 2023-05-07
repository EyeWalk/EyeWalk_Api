package com.insane.eyewalk.api.service;

import com.insane.eyewalk.api.model.Plan;
import com.insane.eyewalk.api.model.input.PlanInput;
import com.insane.eyewalk.api.model.view.PlanView;
import com.insane.eyewalk.api.repositories.PlanRepository;
import com.insane.eyewalk.api.security.auth.AuthenticationService;
import com.insane.eyewalk.api.user.Permission;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class PlanService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Method to create a new plan. Request user must have a permission to create
     * @param planInput Plan Input body required
     * @param principal logged user principal
     * @return Returns a Plan View type if created otherwise returns null
     */
    public Plan createPlan(PlanInput planInput, Principal principal) {
        if (authenticationService.validatePermission(principal, Permission.EDITOR_CREATE)) {
            Plan plan = modelMapper.map(planInput, Plan.class);
            return planRepository.save(plan);
        }
        return null;
    }

    /**
     *
     * Method to get plan details from its id. Request user must send a valid plan id.
     * @param id Plan id
     * @return Returns 200 OK if deleted or 403 if permission is denied
     */
    public Plan getPlan(long id) {
        return planRepository.findById(id).orElseThrow();
    }

    /**
     *
     * Method to delete a plan. Request user must have a permission to delete.
     * @param id Plan id
     * @param principal logged user principal
     * @return Returns 200 OK if deleted or 403 if permission is denied
     */
    public HttpStatus deletePlan(long id, Principal principal) {
        if (authenticationService.validatePermission(principal, Permission.ADMIN_DELETE)) {
            planRepository.delete(planRepository.findById(id).orElseThrow());
            return HttpStatus.OK;
        }
        return HttpStatus.FORBIDDEN;
    }

}
