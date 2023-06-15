package com.hckst.respal.members.application;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MembersService {
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

    private static final String RESET_PASSWORD_DIRECTION_WEB_PREFIX = "http://localhost:3000/reset/password?uid=";
    private static final String RESET_PASSWORD_DIRECTION_APP_PREFIX = "app://reset/password?uid=";
    private static final String MAIL_TITLE = "[Respal] 비밀번호 재설정 링크입니다.";
    private static final String MAIL_MESSAGE = "비밀번호 변경을 원하시면 아래 링크를 통해 변경하실 수 있습니다. \n";
    private static final String SECURITY_ALGORITHM = "AES/ECB/PKCS5Padding";

    @Value("${email.secret-key}")
    // AES 알고리즘 이기 때문에 16바이트의 SecretKey를 사용해야함
    private String secretKey;

    // 로그인 체크
    public Token loginMembers(MembersLoginRequestDto membersLoginRequestDto){
        // 이메일이 존재하지 않는경우
        Members members = membersRepository.findMembersByEmail(membersLoginRequestDto.getEmail()).orElseThrow(
                () -> new InvalidMembersException()
        );
        if(!matchPassword(membersLoginRequestDto.getPassword(),members.getPassword())){ // 비밀번호가 일치하지 않을경우
            throw new InvalidMembersException();
        }
        return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoles());
    }
    
    // email 중복체크 ,, 중복이면 true 없으면 false
    public boolean duplicationCheckEmail(String email){
        return membersRepository.existsMembersByEmail(email);
    }

    // 회원가입 서비스
    @Transactional // insert query,, read-only false
    public Token joinMembers(MembersJoinRequestDto membersJoinRequestDto){
        if(duplicationCheckEmail(membersJoinRequestDto.getEmail())){
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
    public void sendEmail(SendEmailRequestDto sendEmailRequestDto) {
        membersRepository.findCommonMembersByEmail(sendEmailRequestDto.getEmail()).orElseThrow(
                () -> new NotExistMembersException()
        );
        String direction = createPasswordResetDirection(sendEmailRequestDto.getEmail());
        String mailMessage = MAIL_MESSAGE + direction;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(MAIL_TITLE);
        message.setTo(sendEmailRequestDto.getEmail());
        message.setText(mailMessage);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new IncorrectMailArgumentException();
        }
    }

    @Transactional
    public void updatePassword(PasswordPatchRequestDto passwordPatchRequestDto){
        try {
            Members members = membersRepository.findCommonMembersByEmail(decryptEmail(passwordPatchRequestDto.getUid())).orElseThrow(
                    () -> new NotExistMembersException());
            members.updatePassword(passwordPatchRequestDto.getPassword()); // 변경감지
        }catch (Exception e){
            log.info(e.getMessage());
            throw new DecryptException();
        }
    }

    // 비밀번호 재설정 Direction 생성 메서드
    private String createPasswordResetDirection(String email){
        try{
            String encryptedEmail = encryptEmail(email);
            String directionUrl = RESET_PASSWORD_DIRECTION_WEB_PREFIX+encryptedEmail;
            return directionUrl;
        }catch (Exception e) {
            log.info(e.getMessage());
            throw new EncryptException();
        }
    }

    // 이메일 암호화 메서드
    private String encryptEmail(String email) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance(SECURITY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(email.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 이메일 복호화 메서드
    private String decryptEmail(String encryptedText) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance(SECURITY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
