package com.hckst.respal.resume.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.RoleType;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ResumeServiceTest {
    @Autowired
    ResumeService resumeService;
    @Autowired
    MembersRepository membersRepository;
    @Autowired
    ResumeRepository resumeRepository;

    private static CreateResumeRequestDto PUBLIC_RESUME_DTO;
    private static CreateResumeRequestDto PRIVATE_RESUME_DTO;
    private static Members OWNER;
    private static Members TAGGED_USER;
    private static Members NOT_TAGGED_USER;

    @BeforeEach
    void initInstance() {
        OWNER = Members.builder()
                .password("pass")
                .roleType(RoleType.ROLE_USER)
                .build();
        TAGGED_USER = Members.builder()
                .password("pass")
                .roleType(RoleType.ROLE_USER)
                .build();
        NOT_TAGGED_USER = Members.builder()
                .password("pass")
                .roleType(RoleType.ROLE_USER)
                .build();

        membersRepository.save(OWNER);
        membersRepository.save(TAGGED_USER);
        membersRepository.save(NOT_TAGGED_USER);

        PUBLIC_RESUME_DTO = CreateResumeRequestDto.builder()
                .title("제목 테스트1")
                .content("내용테스트 1")
                .resumeType("public")
                .resumeFileId(1L)
                .build();

        PRIVATE_RESUME_DTO = CreateResumeRequestDto.builder()
                .title("제목 테스트 2")
                .content("내용 테스트 2")
                .resumeType("private")
                .resumeFileId(1L)
                .tagIdList(Arrays.asList(TAGGED_USER.getId()))
                .build();
    }

    @Test
    void 이력서_조회_테스트_공개_이력서_정상_케이스() {
        // given
        ResumeDetailResponseDto resume = resumeService.createResume(PUBLIC_RESUME_DTO, OWNER);

        // when
        ResumeDetailResponseDto resumeDetailByResumeId = resumeService.getResumeDetailByResumeId(resume.getResumeId(), TAGGED_USER);

        // then
        assertThat(resumeDetailByResumeId.getViews()).isEqualTo(resume.getViews()+1);
    }

    @Test
    void 이력서_조회_테스트_비공개_이력서_태그_사용자_조회_시_정상() {
        // given
        ResumeDetailResponseDto resume = resumeService.createResume(PRIVATE_RESUME_DTO, OWNER);

        // when
        ResumeDetailResponseDto resumeDetailByResumeId = resumeService.getResumeDetailByResumeId(resume.getResumeId(), TAGGED_USER);

        // then
        assertThat(resumeDetailByResumeId.getViews()).isEqualTo(resume.getViews()+1);
    }

    @Test
    void 이력서_조회_테스트_비공개_이력서_미태그_사용자_조회_시_예외_발생() {
        // given
        ResumeDetailResponseDto resume = resumeService.createResume(PRIVATE_RESUME_DTO, OWNER);

        // when
        // then
        Assertions.assertThatThrownBy(() -> resumeService.getResumeDetailByResumeId(resume.getResumeId(), NOT_TAGGED_USER))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("이력서 조회 권한이 없어요");
    }
}
