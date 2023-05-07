package com.insane.eyewalk.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_PLAN")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Long id;

    @Column(name = "nm_plan", length = 50)
    private String name;

    @Column(name = "ds_description", length = 250)
    private String description;

    @Column(name = "vl_price", precision = 6, scale = 2)
    private BigDecimal price;

}