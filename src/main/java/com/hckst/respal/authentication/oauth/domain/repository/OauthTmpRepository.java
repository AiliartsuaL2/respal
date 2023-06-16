package com.hckst.respal.authentication.oauth.domain.repository;

import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import com.hckst.respal.converter.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthTmpRepository extends JpaRepository<OauthTmp, Long> {

    public Optional<OauthTmp> findOauthTmpByUid(String uid);
    public Optional<OauthTmp> findOauthTmpByUserInfoEmailAndProvider(String email, Provider provider);
}
