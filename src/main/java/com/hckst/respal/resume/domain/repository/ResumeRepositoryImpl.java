package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.converter.ResumeSort;
import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.QResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;
import com.hckst.respal.tag.domain.QTag;
import com.hckst.respal.tag.domain.Tag;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hckst.respal.comment.domain.QComment.comment;
import static com.hckst.respal.members.domain.QJob.job;
import static com.hckst.respal.members.domain.QMembers.members;
import static com.hckst.respal.resume.domain.QResume.resume;
import static com.hckst.respal.resume.domain.QResumeFile.resumeFile;
import static com.hckst.respal.tag.domain.QTag.tag;

@Repository
@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    /**
     * 이력서 조회 query
     * 조회하는곳이 Hub인경우 public한 resume List만 조회
     * 조회하는곳이 Tagged인경우 private한 resume중, tagList에 조회하는 member가 있는 경우에만 조회
     */
    @Override
    public ResumeListResponseDto findResumeListByConditions(ResumeListRequestDto requestDto) {
        List<ResumeDetailResponseDto> result = queryFactory
                .select(new QResumeDetailResponseDto(
                        resume.id,
                        resume.title,
                        resume.content,
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
                .innerJoin(resume.resumeFile, resumeFile)
                .leftJoin(resume.commentList, comment)
                .leftJoin(resume.tagList, tag).on(addJoinConditionByResumeType(requestDto.getResumeType(),requestDto.getViewer()))
                .where(resume.deleteYn.eq(TFCode.FALSE)
                        .and(resume.resumeType.eq(requestDto.getResumeType()))
                        .and(resumeFile.deleteYn.eq(TFCode.FALSE))
                        .and(jobIdContains(requestDto.getJobId()))
                        .and(addWhereConditionByResumeType(requestDto.getResumeType())))
                .groupBy(resume.id)
                .limit(requestDto.getLimit())
                .offset(requestDto.getOffset())
                .orderBy(resumeSort(requestDto.getSort()))
                .fetch();

        long count = queryFactory.select(resume.count())
                .from(resume)
                .innerJoin(resume.members, members)
                .innerJoin(members.job, job)
                .leftJoin(resume.tagList, tag).on(addJoinConditionByResumeType(requestDto.getResumeType(),requestDto.getViewer()))
                .where(resume.deleteYn.eq(TFCode.FALSE)
                        .and(resume.resumeType.eq(requestDto.getResumeType()))
                        .and(jobIdContains(requestDto.getJobId()))
                        .and(addWhereConditionByResumeType(requestDto.getResumeType()))
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
                .innerJoin(resume.resumeFile, resumeFile).fetchJoin()
                .innerJoin(resume.members, members).fetchJoin()
                .where(resumeIdCondition(id)
                        .and(resume.deleteYn.eq(TFCode.FALSE))
                        .and(resumeFile.deleteYn.eq(TFCode.FALSE))
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
    private BooleanExpression addJoinConditionByResumeType(ResumeType resumeType, Members viewer){
        return ResumeType.PRIVATE.equals(resumeType) ? tag.members.eq(viewer) : tag.isNull();
    }
    private BooleanExpression addWhereConditionByResumeType(ResumeType resumeType){
        return ResumeType.PRIVATE.equals(resumeType) ? tag.isNotNull() : null;
    }
    private BooleanExpression jobIdContains(Integer jobId) {
        return jobId != null ? job.id.eq(jobId) : null;
    }

    private BooleanExpression resumeIdCondition(Long id) {
        return id != null ? resume.id.eq(id) : null;
    }


}
