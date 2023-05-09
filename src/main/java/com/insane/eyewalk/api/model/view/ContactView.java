package com.insane.eyewalk.api.model.view;

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
public class ContactView {

    private Long id;
    private String name;
    private boolean emergency = false;
    private List<PhoneView> phones = new ArrayList<>();
    private List<EmailView> emails = new ArrayList<>();
    private List<PictureView> pictures = new ArrayList<>();

}
