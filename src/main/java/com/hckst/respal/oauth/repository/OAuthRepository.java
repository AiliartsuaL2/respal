package com.hckst.respal.oauth.repository;

import com.hckst.respal.domain.Oauth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthRepository extends JpaRepository<Oauth,Long> {
}
