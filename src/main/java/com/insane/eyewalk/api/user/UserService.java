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

    public boolean validateCredential(RegisterRequest registerRequest, String passwordVerify) {
        return (
                !registerRequest.getName().isEmpty() &&
                !registerRequest.getEmail().isEmpty() &&
                !registerRequest.getPassword().isEmpty() &&
                (registerRequest.getPassword().length() >= 5) &&
                (registerRequest.getPassword().equals(passwordVerify))
        );
    }

    public User getUser(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

}