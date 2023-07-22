package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.converter.TFCode;
import com.hckst.respal.members.domain.Job;
import com.hckst.respal.resume.domain.QResume;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.QResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public List<ResumeDetailResponseDto> findResumeListByConditions(ResumeListRequestDto requestDto) {
        queryFactory.select(new QResumeDetailResponseDto(
                        resume.id,
                        resume.title,
                        resume.content,
                        resume.filePath,
                        resume.views,
                        members.id,
                        members.nickname,
                        members.picture,
                        resume.mainYn,
                        resume.modifyYn,
                        resume.regTime,
                        resume.modifyTime))
                .from(resume)
                .innerJoin(resume.members, members)
                .where(resume.deleteYn.eq(TFCode.FALSE)
                        .and(jobIdContains(requestDto.getJob()))
                );
        return null;
    }


    @Override
    public Optional<Resume> findResumeJoinWithMembersById(long id) {
        Resume result = queryFactory.select(resume)
                .from(resume)
                .innerJoin(resume.members, members).fetchJoin()
                .where(resumeIdCondition(id)
                    .and(resume.deleteYn.eq(TFCode.FALSE))
                )
                .fetchOne();
        return Optional.ofNullable(result);
    }


    private BooleanExpression jobIdContains(Job job) {
        return job != null ? resume.members.job.eq(job) : null;
    }

    private BooleanExpression resumeIdCondition(Long id) {
        return id != null ? resume.id.eq(id) : null;
    }

}
