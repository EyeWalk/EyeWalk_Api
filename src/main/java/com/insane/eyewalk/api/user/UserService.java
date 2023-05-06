package com.insane.eyewalk.api.user;

import com.insane.eyewalk.api.security.auth.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean existsAdmin() {
        return userRepository.count() > 0;
    }

    public boolean validateRegisterRequest(RegisterRequest registerRequest, String passwordVerify) {
        return (
                !registerRequest.getName().isEmpty() &&
                !registerRequest.getEmail().isEmpty() &&
                !registerRequest.getPassword().isEmpty() &&
                (registerRequest.getPassword().length() >= 5) &&
                (registerRequest.getPassword().equals(passwordVerify))
        );
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