package com.insane.eyewalk.api.repositories;

import com.insane.eyewalk.api.model.domain.Contact;
import com.insane.eyewalk.api.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    int countAllByUserId(long id);
    List<Contact> findAllByUser(User user);
    Contact findByIdAndUser(long id, User user);
}
