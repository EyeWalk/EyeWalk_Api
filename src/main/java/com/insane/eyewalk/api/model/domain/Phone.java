package com.insane.eyewalk.api.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_PHONE")
public class Phone  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phone")
    private Long id;

    @Column(name = "nr_phone", length = 50)
    private String phone;

    @Column(name = "ds_type", length = 50)
    private String type;

}