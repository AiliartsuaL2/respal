package com.hckst.respal.authentication.jwt.repository;


import com.hckst.respal.authentication.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByKeyId(Long keyId);

    boolean existsByKeyId(Long keyId);

    void deleteByKeyId(Long keyId);
}
