package com.hckst.respal.resume.application;

import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ResumeServiceTest {
    @Autowired
    ResumeService resumeService;
    @Autowired
    MembersRepository membersRepository;
    @Test
    void createResume() {
        //given
        String existMembersEmail = "ailiartsual2@gmail.com";
        Members members = membersRepository.findCommonMembersByEmail(existMembersEmail).get();
        CreateResumeRequestDto resumeRequestDto = CreateResumeRequestDto.builder()
                .title("제목 테스트")
                .content("내용 테스트")
                .filePath("파일 경로")
                .members(members)
                .build();
        resumeService.createResume(resumeRequestDto);
        //when
        ResumeDetailResponseDto resumeDetail = resumeService.getResumeDetail(1L);
        //then
        Assertions.assertThat(resumeDetail.getMembersId()).isEqualTo(6L);
    }
}