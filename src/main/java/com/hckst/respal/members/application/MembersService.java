package com.hckst.respal.members.application;

import com.hckst.respal.converter.RoleType;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.Role;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.members.domain.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MembersService {
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    public Token loginMembers(MembersJoinRequestDto membersJoinRequestDto){
        // 존재하지 않는 회원인경우
        Optional<Members> members = membersRepository.findMembersByEmailAndPassword(membersJoinRequestDto.getEmail(), membersJoinRequestDto.getPassword());
        return members.isEmpty() ? null : jwtTokenProvider.createTokenWithRefresh(members.get().getEmail(), members.get().getRoles());
    }
    
    // email 중복체크 ,, 이미 존재하는경우 false, 존재하지 않는경우 true
    public boolean duplicationCheckEmail(String email){
        return membersRepository.existsMembersByEmail(email);
    }

    // 회원가입,, 중복체크가 되었다고 가정
    @Transactional // insert query,, read-only false
    public void joinMembers(MembersJoinRequestDto membersJoinRequestDto){
        Role role = new Role(RoleType.ROLE_USER);
        Members members = Members.builder()
                .email(membersJoinRequestDto.getEmail())
                .password(membersJoinRequestDto.getPassword())
                .nickname(membersJoinRequestDto.getNickname())
                .role(role)
                .build();
        membersRepository.save(members);
    }


}
