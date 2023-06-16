package com.hckst.respal.members.application;

import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.RoleType;
import com.hckst.respal.exception.members.*;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.Role;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.presentation.dto.request.MembersLoginRequestDto;
import com.hckst.respal.members.presentation.dto.request.PasswordPatchRequestDto;
import com.hckst.respal.members.presentation.dto.request.SendEmailRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MembersService {
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OauthTmpRepository oauthTmpRepository;
    private final JavaMailSender mailSender;


    private static final String RESET_PASSWORD_DIRECTION_WEB_PREFIX = "http://localhost:3000/reset/password?uid=";
    private static final String RESET_PASSWORD_DIRECTION_APP_PREFIX = "app://reset/password?uid=";
    private static final String MAIL_TITLE = "[Respal] 비밀번호 재설정 링크입니다.";
    private static final String MAIL_MESSAGE = "비밀번호 변경을 원하시면 아래 링크를 통해 변경하실 수 있습니다. \n";

    // 로그인 체크
    public Token loginMembers(MembersLoginRequestDto membersLoginRequestDto) {
        // 이메일이 존재하지 않는경우
        Members members = membersRepository.findMembersByEmail(membersLoginRequestDto.getEmail()).orElseThrow(
                () -> new InvalidMembersException()
        );
        if (!matchPassword(membersLoginRequestDto.getPassword(), members.getPassword())) { // 비밀번호가 일치하지 않을경우
            throw new InvalidMembersException();
        }
        return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoles());
    }

    // email 중복체크 ,, 중복이면 true 없으면 false
    public boolean duplicationCheckEmail(String email) {
        return membersRepository.existsMembersByEmail(email);
    }

    // 회원가입 서비스
    @Transactional // insert query,, read-only false
    public Token joinMembers(MembersJoinRequestDto membersJoinRequestDto) {
        if (duplicationCheckEmail(membersJoinRequestDto.getEmail())) {
            throw new DuplicateEmailException();
        }

        Role role = new Role(RoleType.ROLE_USER);
        Members members = Members.builder()
                .email(membersJoinRequestDto.getEmail())
                .password(membersJoinRequestDto.getPassword())
                .picture(membersJoinRequestDto.getPicture())
                .nickname(membersJoinRequestDto.getNickname())
                .role(role)
                .build();
        membersRepository.save(members);

        return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoles());
    }

    // 암호화된 비밀번호가 일치하는지 확인하는 메서드
    public boolean matchPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, encodedPassword);
    }


    // 비밀번호 재설정 direction 설정
    @Transactional
    // Async 설정
//    @Async Annotation을 사용할 때 아래와 같은 세 가지 사항을 주의하자.
//      private method는 사용 불가
//      self-invocation(자가 호출) 불가, 즉 inner method는 사용 불가
//      QueueCapacity 초과 요청에 대한 비동기 method 호출시 방어 코드 작성
    @Async
    public void sendEmail(SendEmailRequestDto sendEmailRequestDto) {
        String mailMessage = MAIL_MESSAGE + sendEmailRequestDto.getUid();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(MAIL_TITLE);
        message.setTo(sendEmailRequestDto.getEmail());
        message.setText(mailMessage);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.info(e.getMessage());
            throw new IncorrectMailArgumentException();
        }
    }
    //회원임을 확인하는 로직 이메일 send는 Async 처리로 controller에서 해당 로직으로 확인 후 메일 전송
    public void checkMembers(SendEmailRequestDto sendEmailRequestDto){
        membersRepository.findCommonMembersByEmail(sendEmailRequestDto.getEmail()).orElseThrow(
                () -> new NotExistMembersException()
        );
    }

    // 비밀번호 변경 후에는 해당 uid 로우를 삭제한다.
    @Transactional
    public void updatePassword(PasswordPatchRequestDto passwordPatchRequestDto) {
        OauthTmp oauthTmp = oauthTmpRepository.findOauthTmpByUid(passwordPatchRequestDto.getUid()).orElseThrow(
                () -> new NotExistPasswordResetDirectionUid()
        );
        Members members = membersRepository.findCommonMembersByEmail(oauthTmp.getUserInfo().getEmail()).orElseThrow(
                () -> new NotExistMembersException());
        members.updatePassword(passwordPatchRequestDto.getPassword()); // 변경감지
        oauthTmpRepository.delete(oauthTmp);
    }

    // 비밀번호 재설정 Direction 생성 메서드
    // 기존에는 암, 복호화로 uid를 설정하였으나, 이렇게 설정시 동일한 URL로 계속 변경이 가능함,
    // async로 인하여 controller호출
    @Transactional
    public String createPasswordResetDirection(SendEmailRequestDto sendEmailRequestDto) {
        String uid = UUID.randomUUID().toString();
        Optional<OauthTmp> optOauthTmp = oauthTmpRepository.findOauthTmpByUserInfoEmailAndProvider(sendEmailRequestDto.getEmail(),Provider.COMMON);
        // 해당 email로 기존에 direction을 만들었던 경우에는 기존것을 삭제하고 새로 만들어줌.
        if(optOauthTmp.isPresent()){
            oauthTmpRepository.delete(optOauthTmp.get());
        }
        OauthTmp oauthTmp = OauthTmp.builder()
                .uid(uid)
                .userInfo(UserInfo.builder().email(sendEmailRequestDto.getEmail()).build())
                .provider(Provider.COMMON)
                .build();
        oauthTmpRepository.save(oauthTmp);
        String directionUrl = RESET_PASSWORD_DIRECTION_WEB_PREFIX + uid;
        return directionUrl;
    }
}
