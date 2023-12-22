package com.hckst.respal.resume.application;

import com.hckst.respal.converter.ResumeType;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    @Commit
    void createResume() {
        //given
        long writerId = 2L;
        long tagMember1 = 6L;
        long tagMember2 = 59L;
        List<Long> tagMembers = Arrays.asList(tagMember2);

        Members writer = membersRepository.findById(writerId).orElseThrow(()-> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        for (int i = 0; i < 4; i++) {
            CreateResumeRequestDto resumeRequestDto = CreateResumeRequestDto.builder()
                    .title("비밀 이력서 "+i)
                    .content("비밀 이력서 내용"+i)
                    .resumeFileId(1L)
                    .tagIdList(tagMembers)
                    .resumeType("private")
                    .build();
            resumeService.createResume(resumeRequestDto, writer);
        }
    }

    @Test
    void getResumeDetail() {
        //given
        long resumeId = 4L;
        //when
        ResumeDetailResponseDto resumeDetailByResumeId = resumeService.getResumeDetailByResumeId(resumeId);
        //then
        assertThat(resumeDetailByResumeId.getMembersNickname()).isEqualTo("asdf");
//        assertThat(resumeDetailByResumeId.getCommentList().size()).isEqualTo(102);
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
    void getHubList(){
        //given
        ResumeListRequestDto resumeListRequestDto = new ResumeListRequestDto(1, 20, 2, "recent", "desc");
        //when
        ResumeListResponseDto result = resumeService.getResumeList(resumeListRequestDto, ResumeType.PUBLIC);
        //then
        assertThat(result.getResumeListCount()).isEqualTo(24);
    }
    @Test
    void getTaggedListSuccess(){
        //given
        ResumeListRequestDto resumeListRequestDto = new ResumeListRequestDto(1, 20, 2, "recent", "desc");
        long viewerId = 6L;
        Members viewer = membersRepository.findById(viewerId).get();
        resumeListRequestDto.setViewer(viewer);
        //when
        ResumeListResponseDto result = resumeService.getResumeList(resumeListRequestDto, ResumeType.PRIVATE);
        //then
        assertThat(result.getResumeListCount()).isEqualTo(17);
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