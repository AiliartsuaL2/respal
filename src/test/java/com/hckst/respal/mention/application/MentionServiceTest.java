package com.hckst.respal.mention.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.mention.domain.Mention;
import com.hckst.respal.mention.domain.repository.MentionRepository;
import com.hckst.respal.mention.presentation.dto.request.AddMentionRequestDto;
import com.hckst.respal.mention.presentation.dto.request.RemoveMentionRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Optional;

@SpringBootTest
@Transactional
class MentionServiceTest {
    @Autowired
    MentionService mentionService;
    @Autowired
    MembersRepository membersRepository;
    @Autowired
    MentionRepository mentionRepository;

    private Members resumeOwner ;
    private Members otherMember ;
    private Members thirdMembersId ;

    private static final long resumeOwnerId = 2L;
    private static final long existOtherMembersId = 6L;
    private static final long existResumeId = 3L;
    private static final long publicResumeId = 8L;
    private static final long exist3rdMembersId = 7L;

    @PostConstruct
    public void initialize(){
        resumeOwner = membersRepository.findById(resumeOwnerId).get();
        otherMember = membersRepository.findById(existOtherMembersId).get();
        thirdMembersId = membersRepository.findById(exist3rdMembersId).get();
    }

    @Test
    void addMentionSuccess() {
        //given
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .members(resumeOwner)
                .resumeId(existResumeId)
                .membersId(existOtherMembersId)
                .build();
        //when
        mentionService.addMention(dto);
        //then
    }

    // 이력서가 존재하지 않는경우
    @Test
    void addMentionFail1() {
        //given
        long notExistResumeId = 4L;
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .members(resumeOwner)
                .resumeId(notExistResumeId)
                .membersId(existOtherMembersId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addMention(dto))
                .isInstanceOf(ApplicationException.class);
    }

    // 존재하지 않는 회원을 언급하려는 경우
    @Test
    void addMentionFail2() {
        //given
        long notExistMembersId = 4L;
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .members(resumeOwner)
                .resumeId(existResumeId)
                .membersId(notExistMembersId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addMention(dto))
                .isInstanceOf(ApplicationException.class);
    }

    // 자기 자신을 언급하려는 경우
    @Test
    void addMentionFail3() {
        //given
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .members(resumeOwner)
                .resumeId(existResumeId)
                .membersId(resumeOwnerId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addMention(dto))
                .isInstanceOf(ApplicationException.class);
    }

    // public resume에서 멘션하려는경우
    @Test
    void addMentionFail4() {
        //given
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .members(resumeOwner)
                .resumeId(publicResumeId)
                .membersId(existOtherMembersId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addMention(dto))
                .isInstanceOf(ApplicationException.class);
    }

    // 자기 게시물이 아닌데 언급하려는 경우
    @Test
    void addMentionFail5() {
        //given
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .members(otherMember)
                .resumeId(existResumeId)
                .membersId(exist3rdMembersId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addMention(dto))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    void removeMentionSuccess(){
        //given
        AddMentionRequestDto dto = AddMentionRequestDto.builder()
                .members(resumeOwner)
                .resumeId(existResumeId)
                .membersId(existOtherMembersId)
                .build();
        mentionService.addMention(dto);

        Members members = membersRepository.findById(existOtherMembersId).get();
        RemoveMentionRequestDto removeMentionRequestDto = RemoveMentionRequestDto.builder()
                .mentionId(1L)
                .members(members)
                .build();
        //when
        mentionService.removeMention(removeMentionRequestDto);
        //then
        Optional<Mention> findedMention = mentionRepository.findById(1L);
        Assertions.assertThat(findedMention).isEqualTo(Optional.empty());
    }
    // 아무 관련 없는 사람이 멘션을 삭제하려는 경우
    @Test
    void removeMentionFail1(){
        long existMentionId = 11L;

        RemoveMentionRequestDto removeMentionRequestDto = RemoveMentionRequestDto.builder()
                .mentionId(existMentionId)
                .members(thirdMembersId)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.removeMention(removeMentionRequestDto))
                .isInstanceOf(ApplicationException.class);
    }
}