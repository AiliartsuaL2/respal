package com.hckst.respal.service;

import com.hckst.respal.domain.Members;
import com.hckst.respal.dto.MemberJoinDto;
import com.hckst.respal.jwt.dto.Token;
import com.hckst.respal.repository.MembersRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        MemberJoinDto memberJoinDto = MemberJoinDto.builder()
                .email(EXIST_MEMBERS_EMAIL_1)
                .password(SUCCESS_PASSWORD)
                .build();
        //when
        Token token = membersService.loginMembers(memberJoinDto);
        //then
        assertThat(token).isNotNull();
    }

    @Test
    void loginMembersFailTest() {
        //given
        MemberJoinDto memberJoinDto = MemberJoinDto.builder()
                .email(EXIST_MEMBERS_EMAIL_1)
                .password(FAIL_PASSWORD)
                .build();
        //when
        Token token = membersService.loginMembers(memberJoinDto);
        //then
        assertThat(token).isNull();
    }

    @Test
    void duplicationCheckEmailSuccessTest() {
        //given

        //when
        boolean duplicationCheckEmail = membersService.duplicationCheckEmail(NEW_EMAIL);

        //then
        assertThat(duplicationCheckEmail).isTrue();

    }
    @Test
    void duplicationCheckEmailFailTest() {
        //given

        //when
        boolean duplicationCheckEmail = membersService.duplicationCheckEmail(EXIST_MEMBERS_EMAIL_1);

        //then
        assertThat(duplicationCheckEmail).isFalse();
    }

    @Test
    void joinMembers() {
        //given
        MemberJoinDto memberJoinDto = MemberJoinDto.builder()
                .email(NEW_EMAIL)
                .password(SUCCESS_PASSWORD)
                .build();
        //when
        membersService.joinMembers(memberJoinDto);
        Optional<Members> membersByEmail = membersRepository.findMembersByEmail(NEW_EMAIL);
        //then
        assertThat(membersByEmail.isPresent()).isTrue();


    }
}