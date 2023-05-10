package com.insane.eyewalk.api.service;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.model.domain.Email;
import com.insane.eyewalk.api.model.input.EmailInput;
import com.insane.eyewalk.api.repositories.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final ModelMapperList modelMapping;

    /**
     * Method to persist a list of emails on database
     * @param emailInputList Email Input List required
     * @return Email List
     */
    public List<Email> saveEmails(List<EmailInput> emailInputList) {
        List<Email> emails = new ArrayList<>();
        for (EmailInput email : emailInputList) {
            emails.add(emailRepository.save(modelMapping.map(email, Email.class)));
        }
        return emails;
    }

}
