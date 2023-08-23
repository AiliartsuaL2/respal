package com.hckst.respal.tag.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.tag.domain.Tag;
import com.hckst.respal.tag.domain.repository.TagRepository;
import com.hckst.respal.tag.presentation.dto.request.AddTagRequestDto;
import com.hckst.respal.tag.presentation.dto.request.RemoveTagRequestDto;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class TagServiceTest {
    @Autowired
    TagService mentionService;
    @Autowired
    MembersRepository membersRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ResumeRepository resumeRepository;

    private Members resumeOwner ;
    private Members otherMember ;
    private Resume resume;
    private List<Long> membersIdList;

    private static final long resumeOwnerId = 2L;
    private static final long existOtherMembersId = 6L;
    private static final long existResumeId = 3L;
    private static final long publicResumeId = 8L;
    private static final long exist3rdMembersId = 7L;
    private static final long exist4thMembersId = 59L;
    private static final long exist5thMembersId = 60L;

    @PostConstruct
    public void initialize(){
        resumeOwner = membersRepository.findById(resumeOwnerId).get();
        otherMember = membersRepository.findById(existOtherMembersId).get();
        resume = resumeRepository.findById(existResumeId).get();

        membersIdList = new ArrayList<>();
        membersIdList.add(existOtherMembersId);
        membersIdList.add(exist3rdMembersId);
        membersIdList.add(exist4thMembersId);
        membersIdList.add(exist5thMembersId);
    }

    @Test
    void addMentionSuccess() {
        //given
        AddTagRequestDto dto = AddTagRequestDto.builder()
                .members(resumeOwner)
                .resumeId(existResumeId)
                .membersIdList(membersIdList)
                .build();
        //when
        mentionService.addTags(dto);
        //then
//        Assertions.assertThat(mentionRepository.findMentionByResume(resume).size()).isEqualTo(4);
    }

    // 이력서가 존재하지 않는경우
    @Test
    void addMentionFail1() {
        //given
        long notExistResumeId = 4L;
        AddTagRequestDto dto = AddTagRequestDto.builder()
                .members(resumeOwner)
                .resumeId(notExistResumeId)
                .membersIdList(membersIdList)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addTags(dto))
                .isInstanceOf(ApplicationException.class);
    }

    // 자기 자신을 언급하려는 경우
    @Test
    void addMentionFail2() {
        //given
        membersIdList.add(resumeOwnerId);

        AddTagRequestDto dto = AddTagRequestDto.builder()
                .members(resumeOwner)
                .resumeId(existResumeId)
                .membersIdList(membersIdList)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addTags(dto))
                .isInstanceOf(ApplicationException.class);
    }

    // public resume에서 멘션하려는경우
    @Test
    void addMentionFail3() {
        //given
        AddTagRequestDto dto = AddTagRequestDto.builder()
                .members(resumeOwner)
                .resumeId(publicResumeId)
                .membersIdList(membersIdList)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addTags(dto))
                .isInstanceOf(ApplicationException.class);
    }

    // 자기 게시물이 아닌데 언급하려는 경우
    @Test
    void addMentionFail4() {
        //given
        AddTagRequestDto dto = AddTagRequestDto.builder()
                .members(otherMember)
                .resumeId(existResumeId)
                .membersIdList(membersIdList)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addTags(dto))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    void removeMentionSuccess(){
        //given
        long existMentionId = 11L;
        RemoveTagRequestDto removeMentionRequestDto = RemoveTagRequestDto.builder()
                .tagId(existMentionId)
                .members(resumeOwner)
                .build();
        //when
        mentionService.removeTag(removeMentionRequestDto);
        //then
        Optional<Tag> findedMention = tagRepository.findById(11L);
        Assertions.assertThat(findedMention).isEqualTo(Optional.empty());
    }
    // 아무 관련 없는 사람이 멘션을 삭제하려는 경우
    @Test
    void removeMentionFail1(){
        long existMentionId = 11L;
        Members thirdMembers = membersRepository.findById(exist3rdMembersId).get();

        RemoveTagRequestDto removeMentionRequestDto = RemoveTagRequestDto.builder()
                .tagId(existMentionId)
                .members(thirdMembers)
                .build();
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.removeTag(removeMentionRequestDto))
                .isInstanceOf(ApplicationException.class);
    }
}