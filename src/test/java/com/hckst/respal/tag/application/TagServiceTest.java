package com.hckst.respal.tag.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.resume.application.ResumeService;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.ResumeFile;
import com.hckst.respal.resume.domain.repository.ResumeFileRepository;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class TagServiceTest {
    @Autowired
    private TagService tagService;
    @Autowired
    private ResumeService resumeService;
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private ResumeFileRepository resumeFileRepository;
    @Autowired
    private MembersRepository membersRepository;

    private static Members RESUME_OWNER;
    private static Members TAGGED_MEMBER_1;
    private static Members TAGGED_MEMBER_2;
    private static Members TAGGED_MEMBER_3;
    private static Members NOT_RELATED_MEMBER;
    private static Resume PRIVATE_RESUME;
    private static Resume PUBLIC_RESUME;
    private static Long NOT_EXIST_RESUME_ID = 1000000L;

    @BeforeEach
    public void init() {
        MembersJoinRequestDto dto1 = MembersJoinRequestDto.builder()
                .email("test@naver.com")
                .password("test1234")
                .picture("picture")
                .nickname("testName")
                .build();
        MembersJoinRequestDto dto2 = MembersJoinRequestDto.builder()
                .email("test2@naver.com")
                .password("test1234")
                .picture("picture")
                .nickname("testName2")
                .build();
        MembersJoinRequestDto dto3 = MembersJoinRequestDto.builder()
                .email("test3@naver.com")
                .password("test1234")
                .picture("picture")
                .nickname("testName3")
                .build();
        MembersJoinRequestDto dto4 = MembersJoinRequestDto.builder()
                .email("test4@naver.com")
                .password("test1234")
                .picture("picture")
                .nickname("testName4")
                .build();
        MembersJoinRequestDto dto5 = MembersJoinRequestDto.builder()
                .email("test5@naver.com")
                .password("test1234")
                .picture("picture")
                .nickname("testName5")
                .build();
        RESUME_OWNER = membersRepository.save(Members.create(dto1));
        TAGGED_MEMBER_1 = membersRepository.save(Members.create(dto2));
        TAGGED_MEMBER_2 = membersRepository.save(Members.create(dto3));
        TAGGED_MEMBER_3 = membersRepository.save(Members.create(dto4));
        NOT_RELATED_MEMBER = membersRepository.save(Members.create(dto5));

        ResumeFile resumeFile = resumeFileRepository.findById(1L).get();
        CreateResumeRequestDto privateDto = CreateResumeRequestDto.builder()
                .title("제목")
                .resumeType("private")
                .resumeFileId(1L)
                .resumeFile(resumeFile)
                .writer(RESUME_OWNER)
                .build();
        CreateResumeRequestDto publicDto = CreateResumeRequestDto.builder()
                .title("제목")
                .resumeType("public")
                .resumeFileId(1L)
                .resumeFile(resumeFile)
                .writer(RESUME_OWNER)
                .build();
        PRIVATE_RESUME = resumeRepository.save(Resume.create(privateDto));
        PUBLIC_RESUME = resumeRepository.save(Resume.create(publicDto));
    }

    @Test
    void 태그_생성_정상_케이스() {
        // given
        List<Long> taggedIdList = List.of(
                TAGGED_MEMBER_1.getId(),
                TAGGED_MEMBER_2.getId(),
                TAGGED_MEMBER_3.getId());

        // when
        tagService.addTags(RESUME_OWNER, PRIVATE_RESUME.getId(), taggedIdList);

        // then
        assertThat(PRIVATE_RESUME.getTagList().size()).isEqualTo(3);
    }

    @Test
    void 태그_생성_공개_이력서_경우_예외_발생() {
        // given
        List<Long> taggedIdList = List.of(
                TAGGED_MEMBER_1.getId(),
                TAGGED_MEMBER_2.getId(),
                TAGGED_MEMBER_3.getId());

        // when
        // then
        assertThatThrownBy(() -> tagService.addTags(RESUME_OWNER, PUBLIC_RESUME.getId(), taggedIdList))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorMessage.CAN_NOT_TAG_PUBLIC_RESUME_EXCEPTION.getMsg());
    }

    @Test
    void 태그_생성_비공개_이력서_다른_사람이_태그시_예외_발생() {
        // given
        List<Long> taggedIdList = List.of(
                TAGGED_MEMBER_1.getId(),
                TAGGED_MEMBER_2.getId(),
                TAGGED_MEMBER_3.getId());

        // when
        // then
        assertThatThrownBy(() -> tagService.addTags(NOT_RELATED_MEMBER, PRIVATE_RESUME.getId(), taggedIdList))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorMessage.PERMISSION_DENIED_TO_TAG_EXCEPTION.getMsg());
    }

    @Test
    void 태그_생성_비공개_이력서_본인_태그시_예외_발생() {
        // given
        List<Long> taggedIdList = List.of(
                RESUME_OWNER.getId(),
                TAGGED_MEMBER_1.getId(),
                TAGGED_MEMBER_2.getId(),
                TAGGED_MEMBER_3.getId());

        // when
        // then
        assertThatThrownBy(() -> tagService.addTags(RESUME_OWNER, PRIVATE_RESUME.getId(), taggedIdList))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorMessage.CAN_NOT_TAG_ONESELF_EXCEPTION.getMsg());
    }

    @Test
    void 태그_제거_이력서_주인_시도_시_정상_제거() {
        // given
        List<Long> taggedIdList = List.of(
                TAGGED_MEMBER_1.getId(),
                TAGGED_MEMBER_2.getId(),
                TAGGED_MEMBER_3.getId());
        tagService.addTags(RESUME_OWNER, PRIVATE_RESUME.getId(), taggedIdList);

        Resume beforeRemoveResume = resumeRepository.findAllResumeById(PRIVATE_RESUME.getId()).get();

        // when
        tagService.removeTag(TAGGED_MEMBER_1.getId(), RESUME_OWNER);
        Resume afterRemovedResume = resumeRepository.findAllResumeById(PRIVATE_RESUME.getId()).get();

        // then
        // 삭제 전, 삭제 후 모두 적용 확인
        assertThat(beforeRemoveResume.getTagList().size()).isEqualTo(2);
        assertThat(afterRemovedResume.getTagList().size()).isEqualTo(2);
    }

    @Test
    void 태그_제거_태그_당한_사람_시도_시_정상_제거() {
        // given
        List<Long> taggedIdList = List.of(
                TAGGED_MEMBER_1.getId(),
                TAGGED_MEMBER_2.getId(),
                TAGGED_MEMBER_3.getId());
        tagService.addTags(RESUME_OWNER, PRIVATE_RESUME.getId(), taggedIdList);

        Resume beforeRemoveResume = resumeRepository.findAllResumeById(PRIVATE_RESUME.getId()).get();

        // when
        tagService.removeTag(TAGGED_MEMBER_1.getId(), TAGGED_MEMBER_1);
        Resume afterRemovedResume = resumeRepository.findAllResumeById(PRIVATE_RESUME.getId()).get();

        // then
        // 삭제 전, 삭제 후 모두 적용 확인
        assertThat(beforeRemoveResume.getTagList().size()).isEqualTo(2);
        assertThat(afterRemovedResume.getTagList().size()).isEqualTo(2);
    }

    @Test
    void 태그_제거_미존재_태그시_예외_발생() {
        // given
        List<Long> taggedIdList = List.of(
                TAGGED_MEMBER_1.getId(),
                TAGGED_MEMBER_2.getId(),
                TAGGED_MEMBER_3.getId());
        tagService.addTags(RESUME_OWNER, PRIVATE_RESUME.getId(), taggedIdList);

        // when
        // then
        assertThatThrownBy(() -> tagService.removeTag(NOT_RELATED_MEMBER.getId(), TAGGED_MEMBER_1))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorMessage.NOT_EXIST_TAG_EXCEPTION.getMsg());
    }

    @Test
    void 태그_제거_연관_없는_사람이_시도_시_예외_발생() {
        // given
        List<Long> taggedIdList = List.of(
                TAGGED_MEMBER_1.getId(),
                TAGGED_MEMBER_2.getId(),
                TAGGED_MEMBER_3.getId());
        tagService.addTags(RESUME_OWNER, PRIVATE_RESUME.getId(), taggedIdList);

        // when
        // then
        assertThatThrownBy(() -> tagService.removeTag(TAGGED_MEMBER_1.getId(), NOT_RELATED_MEMBER))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ErrorMessage.PERMISSION_DENIED_TO_DELETE_EXCEPTION.getMsg());
    }
}