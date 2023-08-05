package com.hckst.respal.members.domain.repository;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.presentation.dto.request.SearchMembersResponseDto;
import com.hckst.respal.members.presentation.dto.response.SearchMembersRequestDto;

import java.util.List;
import java.util.Optional;

public interface MembersRepositoryCustom {
    Optional<MembersOAuthDto> findMembersOauthForLogin(String email, Provider provider);
    Boolean existsMembersOauthForJoin(String email, Provider provider);
    Optional<Members> findCommonMembersByEmail(String email);

    Optional<Members> findMembersAndCommentById(long membersId);
    Optional<Members> findMembersAndResumeById(long membersId);
    List<SearchMembersResponseDto> findMembersByNickname(SearchMembersRequestDto searchMembersRequestDto);
}
