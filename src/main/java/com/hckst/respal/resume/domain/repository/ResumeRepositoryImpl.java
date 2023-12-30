package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.resume.domain.ResumeSort;
import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.QResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
     * 조회하는곳이 myPage인 경우, All Type의 resume 중 본인 이력서 List만 조회
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
                        resume.modifyYn,
                        resume.regTime,
                        resume.modifyTime))
                .from(resume)
                .innerJoin(resume.members, members)
                .innerJoin(resume.resumeFile, resumeFile)
                .leftJoin(resume.tagList, tag).on(addJoinTagCondition(requestDto.getResumeType(),requestDto.getViewer()))
                .where(resume.deleteYn.eq(TFCode.FALSE)
                        .and(resumeFile.deleteYn.eq(TFCode.FALSE))
                        .and(addResumeTypeCondition(requestDto.getResumeType()))
                        .and(addMembersCondition(requestDto.getResumeType(), requestDto.getViewer())))
                .groupBy(resume.id)
                .limit(requestDto.getLimit())
                .offset(requestDto.getOffset())
                .orderBy(resumeSort(requestDto.getSort()))
                .fetch();

        long count = queryFactory.select(resume.count())
                .from(resume)
                .innerJoin(resume.members, members)
                .leftJoin(resume.tagList, tag).on(addJoinTagCondition(requestDto.getResumeType(),requestDto.getViewer()))
                .where(resume.deleteYn.eq(TFCode.FALSE)
                        .and(addResumeTypeCondition(requestDto.getResumeType()))
                        .and(addMembersCondition(requestDto.getResumeType(), requestDto.getViewer())))
                .fetchOne();

        ResumeListResponseDto resumeList = ResumeListResponseDto.builder()
                .resumeList(result)
                .resumeListCount(count)
                .build();

        return resumeList;
    }

    private BooleanExpression addMembersCondition(ResumeType resumeType, Members viewer) {
        // 이력서 타입이 ALL -> 본인이 생성한 이력서
        if(ResumeType.ALL.equals(resumeType)) {
            return members.eq(viewer);
        }
        return null;
    }


    @Override
    public Optional<Resume> findAllResumeById(long id) {
        Resume result = queryFactory.select(resume)
                .from(resume)
                .innerJoin(resume.resumeFile, resumeFile).fetchJoin()
                .innerJoin(resume.members, members).fetchJoin()
                .leftJoin(resume.tagList, tag).fetchJoin()
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

    private BooleanExpression addJoinTagCondition(ResumeType resumeType, Members viewer){
        if(ResumeType.ALL.equals(resumeType)) {
            return Expressions.asBoolean(true).isTrue();
        }
        if(ResumeType.PRIVATE.equals(resumeType)) {
            return tag.members.eq(viewer);
        }
        return tag.isNull();
    }

    private BooleanExpression addResumeTypeCondition(ResumeType resumeType) {
        if(ResumeType.ALL.equals(resumeType)) {
            return null;
        }
        return resume.resumeType.eq(resumeType);
    }

    private BooleanExpression resumeIdCondition(Long id) {
        return id != null ? resume.id.eq(id) : null;
    }
}
