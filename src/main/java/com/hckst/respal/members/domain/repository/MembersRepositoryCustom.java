package com.hckst.respal.members.domain.repository;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;

import java.util.Optional;

public interface MembersRepositoryCustom {
    Optional<MembersOAuthDto> findMembersOauthForLogin(String email, Provider provider);
    Boolean existsMembersOauthForJoin(String email, Provider provider);
    Optional<Members> findCommonMembersByEmail(String email);

    Optional<Members> findMembersAndCommentById(long membersId);
    Optional<Members> findMembersAndResumeById(long membersId);
}
