package com.insane.eyewalk.api.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserView {

    private long id;
    private String name;
    private String email;
    private boolean active;
    private LocalDate created;
    private LocalDate lastVisit;
    private PlanView plan;

}