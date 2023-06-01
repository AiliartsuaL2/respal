package com.hckst.respal.authentication.oauth.domain.repository;

import com.hckst.respal.members.domain.Members;
import com.hckst.respal.authentication.oauth.domain.Oauth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthRepository extends JpaRepository<Oauth,Long> {
    public Oauth findOauthByMembersId(Members members);
}
