package com.insane.eyewalk.api.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanView {

    private long id;
    private String name;
    private String description;
    private BigDecimal price;

}
