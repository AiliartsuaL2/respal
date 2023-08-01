package com.hckst.respal.resume.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Transactional
class ResumeServiceTest {
    @Autowired
    ResumeService resumeService;
    @Autowired
    MembersRepository membersRepository;
    @Autowired
    ResumeRepository resumeRepository;
    @Test
    void createResume() {
        //given
        long existMembersId = 6L;
        CreateResumeRequestDto resumeRequestDto = CreateResumeRequestDto.builder()
                    .title("제목 테스트")
                    .content("내용 테스트")
                    .resumeFileId(1L)
                    .build();
        Members members = membersRepository.findById(existMembersId).orElseThrow(()-> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        resumeService.createResume(resumeRequestDto, members);
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

    // 조회수 테스트
    @Test
    void getResumeDetailViews() {
        //given
        long membersId = 7L;
        CreateResumeRequestDto dto = CreateResumeRequestDto.builder()
                .title("이력서의 제목 테스트")
                .content("이력서의 내용 테스트")
                .resumeFileId(1L)
                .build();
        Members members = membersRepository.findById(membersId).orElseThrow(()-> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        //when
        ResumeDetailResponseDto savedResume = resumeService.createResume(dto, members);
        //then
        ResumeDetailResponseDto foundResume = resumeService.getResumeDetailByResumeId(savedResume.getResumeId());
        assertThat(foundResume.getViews()).isEqualTo(1);
    }
    @Test
    void getResumeList(){
        //given
        ResumeListRequestDto resumeListRequestDto = new ResumeListRequestDto(1, 20, 2, "recent", "desc");
        //when
        ResumeListResponseDto result = resumeService.getResumeList(resumeListRequestDto);
        //then
        for (ResumeDetailResponseDto resumeDetailResponseDto : result.getResumeList()) {
            System.out.println("resumeDetailResponseDto = " + resumeDetailResponseDto.getTitle());
        }
    }
    @Test
    void deleteResume(){
        //given
        long resumeId = 3L;
        long membersId = 2L;
        Members members = membersRepository.findById(membersId).get();
        //when
        resumeService.removeResume(resumeId,members);
        boolean empty = resumeRepository.findById(resumeId).isEmpty();
        //then
        assertThat(empty).isTrue();
    }
}