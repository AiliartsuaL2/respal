package com.hckst.respal.members.domain.repository;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.presentation.dto.response.MembersResponseDto;
import com.hckst.respal.members.presentation.dto.request.SearchMembersRequestDto;

import java.util.List;
import java.util.Optional;

public interface MembersRepositoryCustom {
    Optional<MembersOAuthDto> findMembersOauthForLogin(String email, Provider provider);
    Boolean existsMembersOauthForJoin(String email, Provider provider);
    Optional<Members> findCommonMembersByEmail(String email);
    Optional<Members> findMembersAndResumeById(long membersId);
    List<MembersResponseDto> findMembersByNickname(SearchMembersRequestDto searchMembersRequestDto);
}
