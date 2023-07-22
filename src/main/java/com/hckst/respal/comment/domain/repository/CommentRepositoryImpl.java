package com.hckst.respal.comment.domain.repository;

import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.comment.presentation.dto.response.QCommentsResponseDto;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.resume.domain.Resume;
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
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Optional<List<CommentsResponseDto>> findCommentsDtoByResume(Resume resume){
        List<CommentsResponseDto> comments = queryFactory.select(new QCommentsResponseDto(
                        comment.id,
                        comment.content,
                        comment.xLocation,
                        comment.yLocation,
                        comment.members.id,
                        comment.members.picture,
                        comment.members.nickname,
                        comment.regTime))
                .from(comment)
                .innerJoin(comment.members, members)
                .where(resumeCondition(resume)
                    .and(comment.deleteYn.eq(TFCode.FALSE))
                )
                .fetch();
        return Optional.ofNullable(comments);
    }


    private BooleanExpression resumeCondition(Resume resume) {
        return resume != null ? comment.resume.eq(resume) : null;
    }

}
