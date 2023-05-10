package com.insane.eyewalk.api.service;

import com.insane.eyewalk.api.config.ModelMapperList;
import com.insane.eyewalk.api.model.domain.Phone;
import com.insane.eyewalk.api.model.input.PhoneInput;
import com.insane.eyewalk.api.repositories.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhoneService {

    private final PhoneRepository phoneRepository;
    private final ModelMapperList modelMapping;

    /**
     * Method to persist a list of phones on database
     * @param phoneInputList Phone Input List required
     * @return Phone List
     */
    public List<Phone> savePhones(List<PhoneInput> phoneInputList) {
        List<Phone> phones = new ArrayList<>();
        for (PhoneInput phone : phoneInputList) {
            phones.add(phoneRepository.save(modelMapping.map(phone, Phone.class)));
        }
        return phones;
    }

}
