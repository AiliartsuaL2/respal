package com.hckst.respal.comment.application;

import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.resume.application.ResumeService;
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
}
