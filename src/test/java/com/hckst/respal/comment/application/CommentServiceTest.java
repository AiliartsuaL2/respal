package com.hckst.respal.comment.application;

import com.hckst.respal.comment.presentation.dto.request.CreateCommentRequestDto;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.resume.application.ResumeService;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Commit
class CommentServiceTest {
    @Autowired
    CommentService commentService;
    @Autowired
    ResumeService resumeService;
    @Autowired
    MembersRepository membersRepository;

    @Test
    void createComment() {
        //given
        CreateCommentRequestDto comment = CreateCommentRequestDto.builder()
                .xLocation(340)
                .yLocation(123)
                .content("댓글 테스트1")
                .build();
        long existMembersId = 2L;
        long existResumeId = 3L;
        //when
        commentService.createComment(comment,existMembersId,existResumeId);

        //then
//        int size = resumeService.getResumeDetailByResumeId(3L).getCommentList().size();
//        assertThat(size).isEqualTo(1);

    }

    @Test
    void deleteComment() {
    }
}