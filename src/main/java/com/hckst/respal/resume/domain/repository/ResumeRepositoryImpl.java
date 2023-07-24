package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.converter.ResumeSort;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.members.domain.Job;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.QResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hckst.respal.comment.domain.QComment.comment;
import static com.hckst.respal.members.domain.QJob.job;
import static com.hckst.respal.members.domain.QMembers.members;
import static com.hckst.respal.resume.domain.QResume.resume;

@Repository
@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public ResumeListResponseDto findResumeListByConditions(ResumeListRequestDto requestDto) {
        List<ResumeDetailResponseDto> result = queryFactory
                .select(new QResumeDetailResponseDto(
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
                        resume.modifyTime,
                        comment.id.count()))
                .from(resume)
                .innerJoin(resume.members, members)
                .innerJoin(members.job, job)
                .leftJoin(resume.commentList, comment)
                .where(resume.deleteYn.eq(TFCode.FALSE)
                        .and(jobIdContains(requestDto.getJobId())))
                .groupBy(resume.id)
                .limit(requestDto.getLimit())
                .offset(requestDto.getOffset())
                .orderBy(resumeSort(requestDto.getSort()))
                .fetch();
        long count = queryFactory.select(resume.count())
                .from(resume)
                .innerJoin(resume.members, members)
                .innerJoin(members.job, job)
                .where(resume.deleteYn.eq(TFCode.FALSE)
                        .and(jobIdContains(requestDto.getJobId()))
                ).fetchOne();

        ResumeListResponseDto resumeList = ResumeListResponseDto.builder()
                .resumeList(result)
                .resumeListCount(count)
                .build();

        return resumeList;
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

    // 정렬조건 메서드
    private OrderSpecifier<?> resumeSort(ResumeSort resumeSort) {
        if(resumeSort == null){
            return null;
        }
        Order direction = resumeSort.getOrder();
        NumberExpression<?> sortCondition = resumeSort.getSortCondition();
        return new OrderSpecifier(direction, sortCondition);
    }

    private BooleanExpression jobIdContains(int jobId) {
        return jobId != 0 ? job.id.eq(jobId) : null;
    }

    private BooleanExpression resumeIdCondition(Long id) {
        return id != null ? resume.id.eq(id) : null;
    }


}
