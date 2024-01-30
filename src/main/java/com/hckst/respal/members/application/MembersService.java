package com.hckst.respal.members.application;

import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.jwt.application.TokenProvider;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.presentation.dto.request.*;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.presentation.dto.request.SearchMembersRequestDto;
import com.hckst.respal.members.presentation.dto.response.MembersResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembersService {
    private final MembersRepository membersRepository;
    private final JavaMailSender mailSender;
    private final JwtService jwtService;

    private static final String RESET_PASSWORD_MAIL_TITLE = "[Respal] 변경된 임시 비밀번호입니다.";
    private static final String JOIN_MAIL_TITLE = "[Respal] 비밀번호 재설정 링크입니다.";
    private static final String MAIL_MESSAGE = "변경된 임시 비밀번호는 아래와 같습니다. \n";

    public Token login(MembersLoginRequestDto membersLoginRequestDto) {
        membersLoginRequestDto.checkRequiredFieldIsNull();
        Members member = findCommonMemberByEmail(membersLoginRequestDto.getEmail());
        member.checkPassword(membersLoginRequestDto.getPassword());
        return jwtService.login(member.getId());
    }

    public Members findCommonMemberByEmail(String email) {
        return membersRepository.findCommonMembersByEmail(email).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
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

    public boolean checkMembers(String email){
        return membersRepository.findCommonMembersByEmail(email).isPresent();
    }

    @Transactional
    public void updatePassword(PasswordPatchRequestDto passwordPatchRequestDto) {
        Members members = membersRepository.findCommonMembersByEmail(passwordPatchRequestDto.getEmail()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        // 비밀번호 검증
        members.checkPassword(passwordPatchRequestDto.getExistPassword());
        // 비밀번호 변경
        members.updatePassword(passwordPatchRequestDto.getNewPassword());
    }

    @Transactional
    public String passwordResetToTmp(String email) {
        Members members = membersRepository.findCommonMembersByEmail(email).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        String password = UUID.randomUUID().toString();
        members.updateTmpPassword(password);
        return password;
    }

    public List<MembersResponseDto> searchMembers(SearchMembersRequestDto searchMembersRequestDto){
        return membersRepository.findMembersByNickname(searchMembersRequestDto);
    }
}
