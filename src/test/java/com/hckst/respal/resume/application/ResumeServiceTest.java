package com.hckst.respal.resume.application;

import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Commit
//@Transactional
class ResumeServiceTest {
    @Autowired
    ResumeService resumeService;
    @Autowired
    MembersRepository membersRepository;
    @Test
    void createResume() {
        //given
        long existMembersId = 6L;
        CreateResumeRequestDto resumeRequestDto = CreateResumeRequestDto.builder()
                    .title("제목 테스트")
                    .content("내용 테스트")
                    .filePath("파일 경로")
                    .build();
        resumeService.createResume(resumeRequestDto, existMembersId);
        //when
        ResumeDetailResponseDto resumeDetail = resumeService.getResumeDetailByResumeId(1L);
        //then
        assertThat(resumeDetail.getMembersId()).isEqualTo(existMembersId);
    }

    @Test
    void getResumeDetail() {
        //given
        long resumeId = 4L;
        //when
        ResumeDetailResponseDto resumeDetailByResumeId = resumeService.getResumeDetailByResumeId(resumeId);
        //then
        assertThat(resumeDetailByResumeId.getMembersNickname()).isEqualTo("asdf");
        assertThat(resumeDetailByResumeId.getCommentList().size()).isEqualTo(102);

    }
}