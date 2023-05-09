package com.insane.eyewalk.api.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insane.eyewalk.api.security.token.Token;
import com.insane.eyewalk.api.security.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_USER")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @Column(name = "nm_user", length = 50)
    private String name;

    @Column(name = "st_email", length = 100)
    private String email;

    @Column(name = "st_pass")
    @JsonIgnore
    private String password;

    @Column(name = "bl_active")
    private boolean active = true;

    @Column(name = "dt_created")
    private LocalDate created = LocalDate.now();

    @Column(name = "dt_last_visit")
    private LocalDate lastVisit = LocalDate.now();

    @Column(name = "dt_plan_start")
    private LocalDate planStart = LocalDate.now();

    @Column(name = "dt_plan_end")
    private LocalDate planEnd = LocalDate.now().plusDays(30);

    @Enumerated(EnumType.STRING)
    @Column(name = "nm_role")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_contact"), name = "tb_user_contact")
    private List<Contact> contacts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private Plan plan;

    /**
     * Checks if the user's plan is not expired
     * @return boolean true if the plan still valid
     */
    public boolean isPlanNonExpired() {
        return planEnd.isBefore(LocalDate.now());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}