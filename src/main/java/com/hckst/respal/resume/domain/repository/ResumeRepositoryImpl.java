package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.QResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hckst.respal.comment.domain.QComment.comment;
import static com.hckst.respal.members.domain.QMembers.members;
import static com.hckst.respal.resume.domain.QResume.resume;

@Repository
@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ResumeDetailResponseDto> findResumeDetailByResumeId(Long resumeId) {
        ResumeDetailResponseDto result = queryFactory
                .select(
                        new QResumeDetailResponseDto(
                                resume.id,
                                resume.content,
                                resume.title,
                                resume.filePath,
                                members.id,
                                members.nickname,
                                members.picture,
                                resume.mainYn,
                                resume.modifyYn,
                                resume.regTime,
                                resume.modifyTime,
                                resume.commentList))
                .from(resume)
                .join(resume.members, members)
                .join(resume.commentList, comment)
                .join(comment.members, members)
                .where(resume.id.eq(resumeId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<ResumeDetailResponseDto> findResumeListByConditions(ResumeListRequestDto requestDto) {
        return null;
    }
}
