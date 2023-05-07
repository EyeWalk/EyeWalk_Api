package com.insane.eyewalk.api.security.token;

import com.insane.eyewalk.api.user.User;
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
@Table(name = "TB_TOKEN")
public class Token {

    @Id
    @GeneratedValue
    @Column(name = "id_token")
    public Integer id;

    @Column(name = "ds_token", unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "ds_type")
    public TokenType tokenType = TokenType.BEARER;

    @Column(name = "bl_revoked")
    public boolean revoked;

    @Column(name = "bl_expired")
    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    public User user;

}
