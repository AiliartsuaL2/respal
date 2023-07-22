package com.hckst.respal.members.application;

import com.hckst.respal.converter.*;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.Role;
import com.hckst.respal.members.domain.repository.JobRepository;
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

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MembersService {
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JobRepository jobRepository;
    private final JavaMailSender mailSender;

    private static final String RESET_PASSWORD_MAIL_TITLE = "[Respal] 변경된 임시 비밀번호입니다.";
    private static final String JOIN_MAIL_TITLE = "[Respal] 비밀번호 재설정 링크입니다.";
    private static final String MAIL_MESSAGE = "변경된 임시 비밀번호는 아래와 같습니다. \n";

    // 로그인 체크
    public Token loginMembers(MembersLoginRequestDto membersLoginRequestDto) {
        // 이메일이 존재하지 않는경우
        Members members = membersRepository.findMembersByEmail(membersLoginRequestDto.getEmail()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.INVALID_MEMBER_EXCEPTION)
        );
        if (!matchPassword(membersLoginRequestDto.getPassword(), members.getPassword())) { // 비밀번호가 일치하지 않을경우
            throw new ApplicationException(ErrorMessage.INVALID_MEMBER_EXCEPTION);
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
            throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
        }

        Role role = new Role(RoleType.ROLE_USER);
        Members members = Members.builder()
                .email(membersJoinRequestDto.getEmail())
                .password(membersJoinRequestDto.getPassword())
                .picture(membersJoinRequestDto.getPicture())
                .nickname(membersJoinRequestDto.getNickname())
                .job(jobRepository.getReferenceById(membersJoinRequestDto.getJobId()))
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

    @Async
    public void sendJoinEmail(SendEmailRequestDto sendEmailRequestDto) {
        // 암호화등 프론트쪽과 체크 로직 고민해보기.
        String certificationNumber = "1234";

        String mailMessage = "인증번호는 "+certificationNumber+" 입니다";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(JOIN_MAIL_TITLE);
        message.setTo(sendEmailRequestDto.getEmail());
        message.setText(mailMessage);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.info(e.getMessage());
            throw new ApplicationException(ErrorMessage.INCORRECT_MAIL_ARGUMENT_EXCEPTION);
        }
    }

    // 비밀번호 재설정 direction 설정
    @Transactional
    // Async 설정
//    @Async Annotation을 사용할 때 아래와 같은 세 가지 사항을 주의하자.
//      private method는 사용 불가
//      self-invocation(자가 호출) 불가, 즉 inner method는 사용 불가
//      QueueCapacity 초과 요청에 대한 비동기 method 호출시 방어 코드 작성
    @Async
    public void sendPasswordResetEmail(SendEmailRequestDto sendEmailRequestDto) {
        String mailMessage = MAIL_MESSAGE + sendEmailRequestDto.getTmpPassword();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(RESET_PASSWORD_MAIL_TITLE);
        message.setTo(sendEmailRequestDto.getEmail());
        message.setText(mailMessage);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.info(e.getMessage());
            throw new ApplicationException(ErrorMessage.INCORRECT_MAIL_ARGUMENT_EXCEPTION);
        }
    }
    //회원임을 확인하는 로직 이메일 send는 Async 처리로 controller에서 해당 로직으로 확인 후 메일 전송
    public boolean checkMembers(SendEmailRequestDto sendEmailRequestDto){
        return membersRepository.findCommonMembersByEmail(sendEmailRequestDto.getEmail()).isPresent();
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(PasswordPatchRequestDto passwordPatchRequestDto) {
        Members members = membersRepository.findCommonMembersByEmail(passwordPatchRequestDto.getEmail()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        // 비밀번호 검증
        if (!matchPassword(passwordPatchRequestDto.getTmpPassword(), members.getPassword())) { // 비밀번호가 일치하지 않을경우
            throw new ApplicationException(ErrorMessage.INVALID_MEMBER_EXCEPTION);
        }
        members.updatePassword(passwordPatchRequestDto.getNewPassword()); // 변경감지

    }

    // 비밀번호 재설정 메서드, UUID로 비밀번호를 설정 후 해당 이메일로 임시 비밀번호를 전송해줌.
    @Transactional
    public String passwordResetToTmp(String email) {
        Members members = membersRepository.findCommonMembersByEmail(email).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        String password = UUID.randomUUID().toString();
        members.updateTmpPassword(password);
        return password;
    }

    // tmpPasswordStatus 확인 메서드 , 비밀번호 재설정 후 로그인시 tmpPasswordStatus 확인 후 False로 다시 처리
    public String checkTmpPasswordStatus(MembersLoginRequestDto membersLoginRequestDto) {
        TFCodeConverter tfCodeConverter = new TFCodeConverter();
        Members members = membersRepository.findCommonMembersByEmail(membersLoginRequestDto.getEmail()).get();
        TFCode passwordTmpYn = members.getPasswordTmpYn();
        String result = tfCodeConverter.convertToDatabaseColumn(passwordTmpYn);
        members.tmpPasswordToFalse();
        return result;
    }
}
