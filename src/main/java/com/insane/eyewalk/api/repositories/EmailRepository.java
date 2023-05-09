package com.insane.eyewalk.api.repositories;

import com.insane.eyewalk.api.model.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
