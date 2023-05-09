package com.insane.eyewalk.api.service;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.repositories.PlanRepository;
import com.insane.eyewalk.api.security.auth.AuthenticationService;
import com.insane.eyewalk.api.security.auth.RegisterRequest;
import com.insane.eyewalk.api.security.enums.Permission;
import com.insane.eyewalk.api.security.enums.Role;
import com.insane.eyewalk.api.model.domain.User;
import com.insane.eyewalk.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final AuthenticationService authenticationService;
    private final ModelMapperList modelMapping;

    /**
     * Method to verify if an administrator user exists
     * @return boolean true if admin role exists
     */
    public boolean adminExists() {
        return userRepository.existsUserByRole(Role.ADMIN);
    }

    /**
     * Verify if user exists on repository
     * @param email user's email
     * @return boolean true if user exists
     */
    public boolean userEmailExists(String email) {
        return userRepository.existsUserByEmail(email);
    }

    /**
     * Get a user from repository
     * @param username user's identification email
     * @return a User if exists otherwise will throw an Exception type UsernameNotFoundException
     * @throws UsernameNotFoundException
     */
    public User getUser(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    /**
     * Method to list all registered users
     * @param principal user logged
     * @return List of users if user has permission or throws an exception
     * @throws IllegalAccessError if user is not active or has no permission
     */
    public List<User> getAll(Principal principal) throws IllegalAccessError {
        if (authenticationService.validatePermission(principal, Permission.EDITOR_READ)) {
            return userRepository.findAll();
        }
        throw new IllegalAccessError("User is not active or has no permission.");
    }

}