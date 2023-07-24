package com.hckst.respal.resume.presentation.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.hckst.respal.resume.presentation.dto.response.QResumeDetailResponseDto is a Querydsl Projection type for ResumeDetailResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QResumeDetailResponseDto extends ConstructorExpression<ResumeDetailResponseDto> {

    private static final long serialVersionUID = 1821140350L;

    public QResumeDetailResponseDto(com.querydsl.core.types.Expression<? extends com.hckst.respal.resume.domain.Resume> resume, com.querydsl.core.types.Expression<Long> commentsCount) {
        super(ResumeDetailResponseDto.class, new Class<?>[]{com.hckst.respal.resume.domain.Resume.class, long.class}, resume, commentsCount);
    }

}

