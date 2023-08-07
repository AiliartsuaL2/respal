package com.hckst.respal.mention.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.mention.domain.Mention;
import com.hckst.respal.mention.domain.repository.MentionRepository;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import com.hckst.respal.resume.presentation.dto.request.AddMentionRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentionService {
    private final MentionRepository mentionRepository;
    private final MembersRepository membersRepository;
    private final ResumeRepository resumeRepository;

    // 이력서에 멘션을 추가하는 메서드
    @Transactional
    public void addMention(AddMentionRequestDto addMentionRequestDto){
        Resume resume = resumeRepository.findById(addMentionRequestDto.getResumeId()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID_EXCEPTION));
        Members members = membersRepository.findById(addMentionRequestDto.getMembersId()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        // 게시물의 작성자가 자기 자신을 언급하는경우
        if(members.equals(resume.getMembers())){
            throw new ApplicationException(ErrorMessage.CAN_NOT_MENTION_ONESELF);
        }
        Mention mention = Mention.builder()
                .resume(resume)
                .members(members)
                .build();
        mentionRepository.save(mention);
    }


}
