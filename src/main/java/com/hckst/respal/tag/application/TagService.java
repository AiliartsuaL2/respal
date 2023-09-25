package com.hckst.respal.tag.application;

import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.tag.domain.Tag;
import com.hckst.respal.tag.domain.repository.TagJdbcRepository;
import com.hckst.respal.tag.domain.repository.TagRepository;
import com.hckst.respal.tag.presentation.dto.request.AddTagRequestDto;
import com.hckst.respal.tag.presentation.dto.request.RemoveTagRequestDto;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;
    private final MembersRepository membersRepository;
    private final ResumeRepository resumeRepository;
    private final TagJdbcRepository tagJdbcRepository;

    /**
     * 이력서에 태그를 추가하는 메서드
     * 신규 생성시에는 createResume에서 같이 처리
     * 편집시에도 updateResume에서 같이 처리(저장 클릭시 한 번에)
     */

    @Transactional
    public void addTags(AddTagRequestDto addTagRequestDto){
        Resume resume = resumeRepository.findById(addTagRequestDto.getResumeId()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID_EXCEPTION));

        // 공개 이력서인데 언급을 시도하는경우
        if(ResumeType.PUBLIC.equals(resume.getResumeType())){
            throw new ApplicationException(ErrorMessage.CAN_NOT_TAG_PUBLIC_RESUME_EXCEPTION);
        }
        // 멘션하려는이가 게시물의 주인이 아닌경우
        if(!addTagRequestDto.getMembers().equals(resume.getMembers())){
            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_TAG_EXCEPTION);
        }
        List<Tag> tagList = new ArrayList<>();
        // in 조건으로 한 번에 membersList를 조회해옴
        List<Members> membersList = membersRepository.findByIdIn(addTagRequestDto.getMembersIdList());
        for(Members members : membersList){
            // 게시물의 작성자가 자기 자신을 언급하는경우
            if(members.equals(resume.getMembers())){
                throw new ApplicationException(ErrorMessage.CAN_NOT_TAG_ONESELF_EXCEPTION);
            }
            Tag tag = Tag.builder()
                    .resume(resume)
                    .members(members)
                    .build();
            tagList.add(tag);
        }
        tagJdbcRepository.saveAll(tagList);
    }
    @Transactional
    public void removeTag(RemoveTagRequestDto removeTagRequestDto){
        // 이력서와 멘션을 조인해서 가져옴
        Tag tag = tagRepository.findTagAndResumeById(removeTagRequestDto.getTagId())
                .orElseThrow(
                        () -> new ApplicationException(ErrorMessage.NOT_EXIST_TAG_EXCEPTION));
        // 삭제의 주체가 이력서의 주인이 아니거나, 멘션당한 사람이 아닌경우
        if(!(removeTagRequestDto.getMembers().equals(tag.getMembers())
                || removeTagRequestDto.getMembers().equals(tag.getResume().getMembers()))){
            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_DELETE_EXCEPTION);
        }
        tagRepository.delete(tag);
    }
}
