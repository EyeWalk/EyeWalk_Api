package com.insane.eyewalk.api.repositories;

import com.insane.eyewalk.api.security.enums.Role;
import com.insane.eyewalk.api.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);

    boolean existsUserByRole(Role role);

}