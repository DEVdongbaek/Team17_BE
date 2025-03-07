package com.kakaoseventeen.dogwalking.token.repository;

import com.kakaoseventeen.dogwalking.token.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Integer> {

    @Query("select r from RefreshToken r where r.token=:refreshToken")
    Optional<RefreshToken> findByToken(String refreshToken);

    boolean existsByEmail(String userEmail);
    void deleteByEmail(String userEmail);
}
