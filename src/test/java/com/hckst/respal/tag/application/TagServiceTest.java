package com.hckst.respal.tag.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.tag.domain.repository.TagRepository;
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
        //when
        mentionService.addTags(resumeOwner, existResumeId, membersIdList);
        //then
//        Assertions.assertThat(mentionRepository.findMentionByResume(resume).size()).isEqualTo(4);
    }

    // 이력서가 존재하지 않는경우
    @Test
    void addMentionFail1() {
        //given
        long notExistResumeId = 4L;
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addTags(resumeOwner, notExistResumeId, membersIdList))
                .isInstanceOf(ApplicationException.class);
    }

    // 자기 자신을 언급하려는 경우
    @Test
    void addMentionFail2() {
        //given
        membersIdList.add(resumeOwnerId);
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addTags(resumeOwner, existResumeId, membersIdList))
                .isInstanceOf(ApplicationException.class);
    }

    // public resume에서 멘션하려는경우
    @Test
    void addMentionFail3() {
        //given
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addTags(resumeOwner, publicResumeId, membersIdList))
                .isInstanceOf(ApplicationException.class);
    }

    // 자기 게시물이 아닌데 언급하려는 경우
    @Test
    void addMentionFail4() {
        //given
        //when
        //then
        Assertions.assertThatThrownBy(() -> mentionService.addTags(resumeOwner, existResumeId, membersIdList))
                .isInstanceOf(ApplicationException.class);
    }
}