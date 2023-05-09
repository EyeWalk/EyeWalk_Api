package com.insane.eyewalk.api.service;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.model.domain.Contact;
import com.insane.eyewalk.api.model.domain.Email;
import com.insane.eyewalk.api.model.domain.Phone;
import com.insane.eyewalk.api.model.domain.User;
import com.insane.eyewalk.api.model.input.ContactInput;
import com.insane.eyewalk.api.model.input.EmailInput;
import com.insane.eyewalk.api.model.input.PhoneInput;
import com.insane.eyewalk.api.repositories.ContactRepository;
import com.insane.eyewalk.api.repositories.EmailRepository;
import com.insane.eyewalk.api.repositories.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailRepository emailRepository;
    private final PhoneRepository phoneRepository;
    private final ModelMapperList modelMapping;

    /**
     * Method to register a new User Contact
     * @param user the logged user requesting to add this contact
     * @param contactInput contact input body required
     * @return Saved Contact
     */
    public Contact registerContact(User user, ContactInput contactInput) {
        List<Email> emails = new ArrayList<>();
        List<Phone> phones = new ArrayList<>();
        for (EmailInput email : contactInput.getEmails()) {
            emails.add(emailRepository.save(modelMapping.map(email, Email.class)));
        }
        for (PhoneInput phone : contactInput.getPhones()) {
            phones.add(phoneRepository.save(modelMapping.map(phone, Phone.class)));
        }
        Contact contact = modelMapping.map(contactInput, Contact.class);
        contact.setUser(user);
        contact.setEmails(emails);
        contact.setPhones(phones);
        return contactRepository.save(contact);
    }

}
