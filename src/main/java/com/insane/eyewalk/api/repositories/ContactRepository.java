package com.insane.eyewalk.api.repositories;

import com.insane.eyewalk.api.model.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    int countAllByUserId(long id);
}
