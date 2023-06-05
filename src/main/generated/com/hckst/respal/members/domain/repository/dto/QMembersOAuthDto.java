package com.hckst.respal.members.domain.repository.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.hckst.respal.members.domain.repository.dto.QMembersOAuthDto is a Querydsl Projection type for MembersOAuthDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMembersOAuthDto extends ConstructorExpression<MembersOAuthDto> {

    private static final long serialVersionUID = -640067878L;

    public QMembersOAuthDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> email) {
        super(MembersOAuthDto.class, new Class<?>[]{long.class, String.class}, id, email);
    }

}

