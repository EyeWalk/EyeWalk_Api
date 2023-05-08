package com.insane.eyewalk.api.service;

import com.insane.eyewalk.api.model.Plan;
import com.insane.eyewalk.api.model.input.PlanInput;
import com.insane.eyewalk.api.repositories.PlanRepository;
import com.insane.eyewalk.api.security.auth.AuthenticationService;
import com.insane.eyewalk.api.user.Permission;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

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
     * @return Returns a Plan type if created otherwise will throw an exception
     * @throws IllegalAccessError if user is not active or has no permission
     */
    public Plan createPlan(PlanInput planInput, Principal principal) throws IllegalAccessError {
        if (authenticationService.validatePermission(principal, Permission.EDITOR_CREATE)) {
            Plan plan = modelMapper.map(planInput, Plan.class);
            return planRepository.save(plan);
        }
        throw new IllegalAccessError("User not active or has no permission");
    }

    /**
     * Method to update a plan. Request user must have a permission to update and send an existing plan id
     * @param planInput Plan Input body required
     * @param principal logged user principal
     * @return Returns a Plan type if created otherwise will throw an exception
     * @throws NoSuchElementException if no plan was found to update
     * @throws IllegalAccessError if user is not active or has no permission
     */
    public Plan updatePlan(long id, PlanInput planInput, Principal principal) throws NoSuchElementException, IllegalAccessError {
        if (authenticationService.validatePermission(principal, Permission.EDITOR_UPDATE)) {
            Plan currentPlan = planRepository.findById(id).orElseThrow();
            Plan updatedPlan = modelMapper.map(planInput, Plan.class);
            if (updatedPlan.getName().isEmpty() || updatedPlan.getName() == null) updatedPlan.setName(currentPlan.getName());
            if (updatedPlan.getDescription().isEmpty() || updatedPlan.getDescription() == null) updatedPlan.setDescription(currentPlan.getDescription());
            updatedPlan.setId(id);
            return planRepository.save(updatedPlan);
        }
        throw new IllegalAccessError("User not active or has no permission");
    }

    /**
     *
     * Method to get plan details from its id. Request user must send a valid plan id.
     * @param id plan id
     * @return a Plan type if found otherwise will throw an exception
     * @throws NoSuchElementException if no element was found
     */
    public Plan getPlan(long id) throws NoSuchElementException {
        return planRepository.findById(id).orElseThrow();
    }

    /**
     *
     * Method to get all plans available.
     * @return a list of plans if none is found will return an empty list
     */
    public List<Plan> listPlans() {
        return planRepository.findAll();
    }

    /**
     *
     * Method to delete a plan. Request user must have a permission to delete and send an existing plan id.
     * @param id Plan id
     * @param principal logged user principal
     * @throws NoSuchElementException if no element was found
     * @throws IllegalAccessError if user is not active or has no permission
     */
    public void deletePlan(long id, Principal principal) throws NoSuchElementException, IllegalAccessError {
        if (authenticationService.validatePermission(principal, Permission.ADMIN_DELETE)) {
            planRepository.delete(planRepository.findById(id).orElseThrow());
        }
        throw new IllegalAccessError("User not active or has no permission");
    }

}
