package com.hckst.respal.members.application;

import com.hckst.respal.converter.RoleType;
import com.hckst.respal.exception.members.DuplicateEmailException;
import com.hckst.respal.exception.members.InvalidMembersException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.Role;
import com.hckst.respal.members.presentation.dto.request.MailDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.presentation.dto.request.MembersLoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MembersService {
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

    // 로그인 체크
    public Token loginMembers(MembersLoginRequestDto membersLoginRequestDto){
        // 이메일이 존재하지 않는경우
        Members members = membersRepository.findMembersByEmail(membersLoginRequestDto.getEmail()).orElseThrow(
                () -> new InvalidMembersException()
        );
        if(!matchPassword(membersLoginRequestDto.getPassword(),members.getPassword())){ // 비밀번호가 일치하지 않을경우
            throw new InvalidMembersException();
        }
        return jwtTokenProvider.createTokenWithRefresh(members.getEmail(), members.getRoles());
    }
    
    // email 중복체크 ,, 중복이면 true 없으면 false
    public boolean duplicationCheckEmail(String email){
        return membersRepository.existsMembersByEmail(email);
    }

    // 회원가입 서비스
    @Transactional // insert query,, read-only false
    public void joinMembers(MembersJoinRequestDto membersJoinRequestDto){
        if(duplicationCheckEmail(membersJoinRequestDto.getEmail())){
            throw new DuplicateEmailException();
        }

        Role role = new Role(RoleType.ROLE_USER);
        Members members = Members.builder()
                .email(membersJoinRequestDto.getEmail())
                .password(membersJoinRequestDto.getPassword())
                .nickname(membersJoinRequestDto.getNickname())
                .role(role)
                .build();
        membersRepository.save(members);
    }

    // 암호화된 비밀번호가 일치하는지 확인하는 메서드
    public boolean matchPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, encodedPassword);
    }

    // 이메일 재설정 direction 설정
    public String sendResetEmailDirection(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getToAddress());
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getMessage());
        message.setFrom(mailDto.getFromAddress());
        message.setReplyTo(mailDto.getFromAddress());
        mailSender.send(message);
        return null;
    }
}
