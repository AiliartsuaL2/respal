package com.hckst.respal.tag.application;

import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.tag.domain.Tag;
import com.hckst.respal.tag.domain.repository.TagRepository;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;
    private final MembersRepository membersRepository;
    private final ResumeRepository resumeRepository;

    @Transactional
    public void addTags(Members members, Long resumeId, List<Long> taggedIdList){
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_EXCEPTION));

        validationForAdd(resume, members, taggedIdList);

        List<Tag> tagList = membersRepository.findByIdIn(taggedIdList).stream()
                .filter(m -> !m.equals(resume.getMembers()))
                .map(m -> new Tag(resume, m))
                .collect(Collectors.toList());
        tagRepository.saveAll(tagList);
    }

    private void validationForAdd(Resume resume, Members writer, List<Long> taggedIdList) {
        // 공개 이력서인데 언급을 시도하는경우
        if(ResumeType.PUBLIC.equals(resume.getResumeType())){
            throw new ApplicationException(ErrorMessage.CAN_NOT_TAG_PUBLIC_RESUME_EXCEPTION);
        }
        // 멘션하려는이가 게시물의 주인이 아닌경우
        if(!resume.getMembers().equals(writer)){
            throw new ApplicationException(ErrorMessage.PERMISSION_DENIED_TO_TAG_EXCEPTION);
        }
        // 본인 이력서에 본인을 언급하는 경우
        if(taggedIdList.contains(writer.getId())) {
            throw new ApplicationException(ErrorMessage.CAN_NOT_TAG_ONESELF_EXCEPTION);
        }
    }

    @Transactional
    public void removeTag(Long deletedMembersId, Members deleteMembers){
        // 이력서와 멘션을 조인해서 가져옴
        Tag tag = tagRepository.findTagAndResumeByMembersId(deletedMembersId)
                .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_EXIST_TAG_EXCEPTION));
        // 삭제의 주체가 멘션당한 사람이 아니거나, 이력서의 주인이 아닌 경우
        if(!(deleteMembers.equals(tag.getMembers())
                || deleteMembers.equals(tag.getResume().getMembers()))){
            throw new ApplicationException(ErrorMessage.PERMISSION_DENIED_TO_DELETE_EXCEPTION);
        }
        tag.remove();
        tagRepository.delete(tag);
    }
}
