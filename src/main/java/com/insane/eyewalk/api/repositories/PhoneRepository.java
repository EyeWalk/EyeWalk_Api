package com.insane.eyewalk.api.repositories;

import com.insane.eyewalk.api.model.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
