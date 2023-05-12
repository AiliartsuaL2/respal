package com.hckst.respal.oauth.repository;

import com.hckst.respal.domain.OAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthRepository extends JpaRepository<OAuth,Long> {
}
