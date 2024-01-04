package com.hckst.respal.members.presentation.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.hckst.respal.members.presentation.dto.response.QMembersResponseDto is a Querydsl Projection type for MembersResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMembersResponseDto extends ConstructorExpression<MembersResponseDto> {

    private static final long serialVersionUID = -73899539L;

    public QMembersResponseDto(com.querydsl.core.types.Expression<Long> membersId, com.querydsl.core.types.Expression<String> nickname, com.querydsl.core.types.Expression<String> picture, com.querydsl.core.types.Expression<String> email) {
        super(MembersResponseDto.class, new Class<?>[]{long.class, String.class, String.class, String.class}, membersId, nickname, picture, email);
    }

}

