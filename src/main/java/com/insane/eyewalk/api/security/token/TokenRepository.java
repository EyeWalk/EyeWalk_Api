package com.insane.eyewalk.api.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = "SELECT token FROM Token token INNER JOIN User user ON token.user.id = user.id WHERE user.id = :id AND (token.expired = false or token.revoked = false)")
    List<Token> findAllValidTokenByUser(Long id);

    Optional<Token> findByToken(String token);
}
