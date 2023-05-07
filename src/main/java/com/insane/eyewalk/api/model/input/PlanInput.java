package com.insane.eyewalk.api.model.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanInput {

    private String name = "";
    private String description = "";
    private BigDecimal price = new BigDecimal("0.00");

}
