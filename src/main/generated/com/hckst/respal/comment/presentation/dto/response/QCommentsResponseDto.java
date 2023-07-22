package com.hckst.respal.comment.presentation.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.hckst.respal.comment.presentation.dto.response.QCommentsResponseDto is a Querydsl Projection type for CommentsResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCommentsResponseDto extends ConstructorExpression<CommentsResponseDto> {

    private static final long serialVersionUID = 1381830696L;

    public QCommentsResponseDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> content, com.querydsl.core.types.Expression<Integer> xLocation, com.querydsl.core.types.Expression<Integer> yLocation, com.querydsl.core.types.Expression<Long> membersId, com.querydsl.core.types.Expression<String> membersPicture, com.querydsl.core.types.Expression<String> membersNickname, com.querydsl.core.types.Expression<java.time.LocalDateTime> regTime) {
        super(CommentsResponseDto.class, new Class<?>[]{long.class, String.class, int.class, int.class, long.class, String.class, String.class, java.time.LocalDateTime.class}, id, content, xLocation, yLocation, membersId, membersPicture, membersNickname, regTime);
    }

}

