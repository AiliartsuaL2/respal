package com.hckst.respal.tag.application;

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
        resume.validateForTag(members);

        List<Tag> tagList = membersRepository.findByIdIn(taggedIdList).stream()
                .map(m -> new Tag(resume, m))
                .collect(Collectors.toList());
        tagRepository.saveAll(tagList);
    }

    @Transactional
    public void removeTag(Long deletedMembersId, Members deleteMembers){
        // 이력서와 멘션을 조인해서 가져옴
        Tag tag = tagRepository.findTagAndResumeByMembersId(deletedMembersId)
                .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_EXIST_TAG_EXCEPTION));
        tag.remove(deleteMembers);
        tagRepository.delete(tag);
    }
}
