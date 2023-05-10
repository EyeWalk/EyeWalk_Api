package com.insane.eyewalk.api.model.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactInput {

    private String name;
    private boolean emergency = false;
    private List<PhoneInput> phones = new ArrayList<>();
    private List<EmailInput> emails = new ArrayList<>();

}
