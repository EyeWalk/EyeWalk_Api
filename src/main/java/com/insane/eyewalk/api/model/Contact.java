package com.insane.eyewalk.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insane.eyewalk.api.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_CONTACT")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contact")
    private Long id;

    @Column(name = "nm_contact", length = 100)
    private String name;

    @Column(name = "bl_emergency")
    private boolean emergency = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = "id_contact"), inverseJoinColumns = @JoinColumn(name = "id_phone"), name = "tb_contact_phone")
    @JsonIgnore
    private List<Phone> phones = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = "id_contact"), inverseJoinColumns = @JoinColumn(name = "id_email"), name = "tb_contact_email")
    @JsonIgnore
    private List<Email> emails = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = "id_contact"), inverseJoinColumns = @JoinColumn(name = "id_picture"), name = "tb_contact_picture")
    @JsonIgnore
    private List<Picture> pictures = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonIgnore
    private User user;

}
