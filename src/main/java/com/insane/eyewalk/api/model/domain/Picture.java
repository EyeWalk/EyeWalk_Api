package com.insane.eyewalk.api.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_PICTURE")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_picture")
    private Long id;

    @Column(name = "nm_picture", length = 100)
    private String filename;

    @Column(name = "nm_extension", length = 5)
    private String extension;

    @Column(name = "dt_created")
    private Date created = new Date();

}