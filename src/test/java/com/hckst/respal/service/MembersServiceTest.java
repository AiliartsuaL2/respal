package com.hckst.respal.service;

import com.hckst.respal.members.application.MembersService;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.presentation.dto.request.MailDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.presentation.dto.request.MembersLoginRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MembersServiceTest {

    @Autowired
    MembersService membersService;
    @Autowired
    MembersRepository membersRepository;
    private static final String EXIST_MEMBERS_EMAIL_1 = "leejuho1712041@gmail.com";
    private static final String EXIST_MEMBERS_EMAIL_2 = "ailiartsual2@gmail.com";
    private static final String EXIST_MEMBERS_EMAIL_3 = "skan03@nate.com";
    private static final String SUCCESS_PASSWORD = "1231234567a";
    private static final String FAIL_PASSWORD = "1122334";
    private static final String NEW_EMAIL = "juho123@naver.com";

    @Test
    void loginMembersSuccessTest() {
        //given
        MembersLoginRequestDto membersLoginRequestDto = MembersLoginRequestDto.builder()
                .email(EXIST_MEMBERS_EMAIL_1)
                .password(SUCCESS_PASSWORD)
                .build();
        //when
        Token token = membersService.loginMembers(membersLoginRequestDto);
        //then
        assertThat(token).isNotNull();
    }

    @Test
    void loginMembersFailTest() {
        //given
        //given
        MembersLoginRequestDto membersLoginRequestDto = MembersLoginRequestDto.builder()
                .email(EXIST_MEMBERS_EMAIL_1)
                .password(FAIL_PASSWORD)
                .build();
        //when
        Token token = membersService.loginMembers(membersLoginRequestDto);
        //then
        assertThat(token).isNull();
    }

    @Test
    void duplicationCheckEmailFailTest() {
        //given

        //when
        boolean duplicationCheckEmail = membersService.duplicationCheckEmail(NEW_EMAIL);

        //then
        assertThat(duplicationCheckEmail).isTrue();

    }
    @Test
    void duplicationCheckEmailSuccessTest() {
        //given

        //when
        boolean duplicationCheckEmail = membersService.duplicationCheckEmail(EXIST_MEMBERS_EMAIL_1);

        //then
        assertThat(duplicationCheckEmail).isFalse();
    }

    @Test
    void joinMembers() {
        //given
        MembersJoinRequestDto membersJoinRequestDto = MembersJoinRequestDto.builder()
                .email(NEW_EMAIL)
                .password(SUCCESS_PASSWORD)
                .build();
        //when
        membersService.joinMembers(membersJoinRequestDto);
        Optional<Members> membersByEmail = membersRepository.findMembersByEmail(NEW_EMAIL);
        //then
        assertThat(membersByEmail.isPresent()).isTrue();

    }
    @Test
    void mailSuccessTest(){
        //given
        MailDto mailDto = MailDto.builder()
                .toAddress("ailiartsual2@gmail.com")
                .title("제목 테스트")
                .message("메일 테스트")
                .fromAddress("respalOfficial@gmail.com")
                .build();
        //when
        membersService.sendResetEmailDirection(mailDto);
        //then
    }

    @Test
    void mailFailedTest(){
        //given
        MailDto mailDto = MailDto.builder()
                .title("제목 테스트")
                .message("메일 테스트")
                .build();
        //when
        membersService.sendResetEmailDirection(mailDto);
        //then
    }
}