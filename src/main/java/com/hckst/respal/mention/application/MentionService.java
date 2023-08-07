package com.hckst.respal.mention.application;

import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.mention.domain.Mention;
import com.hckst.respal.mention.domain.repository.MentionRepository;
import com.hckst.respal.mention.presentation.dto.request.AddMentionRequestDto;
import com.hckst.respal.mention.presentation.dto.request.RemoveMentionRequestDto;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
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

        // 멘션하려는이가 게시물의 주인이 아닌경우
        if(!addMentionRequestDto.getMembers().equals(resume.getMembers())){
            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_MENTION_EXCEPTION);
        }
        Members members = membersRepository.findById(addMentionRequestDto.getMembersId()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION));
        // 공개 이력서인데 언급을 시도하는경우
        // TODO 목요일 회의시 이야기해보기
        if(ResumeType.PUBLIC.equals(resume.getResumeType())){
            throw new ApplicationException(ErrorMessage.CAN_NOT_MENTION_PUBLIC_RESUME_EXCEPTION);
        }
        // 게시물의 작성자가 자기 자신을 언급하는경우
        if(members.equals(resume.getMembers())){
            throw new ApplicationException(ErrorMessage.CAN_NOT_MENTION_ONESELF_EXCEPTION);
        }
        Mention mention = Mention.builder()
                .resume(resume)
                .members(members)
                .build();
        mentionRepository.save(mention);
    }
    @Transactional
    public void removeMention(RemoveMentionRequestDto removeMentionRequestDto){
        // 이력서와 멘션을 조인해서 가져옴
        Mention mention = mentionRepository.findMentionAndResumeById(removeMentionRequestDto.getMentionId())
                .orElseThrow(
                        () -> new ApplicationException(ErrorMessage.NOT_EXIST_MENTION_EXCEPTION));
        // 삭제의 주체가 이력서의 주인이 아니거나, 멘션당한 사람이 아닌경우
        if(!(removeMentionRequestDto.getMembers().equals(mention.getMembers())
                || removeMentionRequestDto.getMembers().equals(mention.getResume().getMembers()))){
            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_DELETE_EXCEPTION);
        }
        mentionRepository.delete(mention);
    }
}
