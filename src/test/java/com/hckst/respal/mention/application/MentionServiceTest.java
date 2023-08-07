package com.hckst.respal.mention.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.resume.presentation.dto.request.AddMentionRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MentionServiceTest {
    @Autowired
    MentionService mentionService;
    private static final long resumeOwnerId = 2L;
    private static final long existOtherMembersId = 6L;
    private static final long existResumeId = 3L;

    @Test
    void addMentionSuccess() {
        //given
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .resumeId(existResumeId)
                .membersId(existOtherMembersId)
                .build();
        //when
        mentionService.addMention(dto);
        //then
    }

    @Test
    void addMentionFail1() {
        //given
        long notExistResumeId = 4L;
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .resumeId(notExistResumeId)
                .membersId(existOtherMembersId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addMention(dto))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    void addMentionFail2() {
        //given
        long notExistMembersId = 4L;
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .resumeId(existResumeId)
                .membersId(notExistMembersId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addMention(dto))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    void addMentionFail3() {
        //given
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .resumeId(existResumeId)
                .membersId(resumeOwnerId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addMention(dto))
                .isInstanceOf(ApplicationException.class);
    }
}