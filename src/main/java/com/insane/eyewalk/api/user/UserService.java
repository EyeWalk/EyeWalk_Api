package com.insane.eyewalk.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

}