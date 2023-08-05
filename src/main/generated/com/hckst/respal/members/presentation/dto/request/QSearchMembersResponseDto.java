package com.hckst.respal.members.presentation.dto.request;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.hckst.respal.members.presentation.dto.request.QSearchMembersResponseDto is a Querydsl Projection type for SearchMembersResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QSearchMembersResponseDto extends ConstructorExpression<SearchMembersResponseDto> {

    private static final long serialVersionUID = 1832853073L;

    public QSearchMembersResponseDto(com.querydsl.core.types.Expression<Long> membersId, com.querydsl.core.types.Expression<String> nickname, com.querydsl.core.types.Expression<String> picture, com.querydsl.core.types.Expression<String> email) {
        super(SearchMembersResponseDto.class, new Class<?>[]{long.class, String.class, String.class, String.class}, membersId, nickname, picture, email);
    }

}

