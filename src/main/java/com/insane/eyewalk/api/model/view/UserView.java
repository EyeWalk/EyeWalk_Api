package com.insane.eyewalk.api.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserView {

    private long id;
    private String name;
    private String email;
    private boolean active;
    private PlanView plan;
    private LocalDate planStart;
    private LocalDate planEnd;
    private List<ContactView> contacts;
    private LocalDate created;
    private LocalDate lastVisit;

}