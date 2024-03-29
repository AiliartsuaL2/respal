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

    public QResumeDetailResponseDto(com.querydsl.core.types.Expression<Long> resumeId, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<Integer> views, com.querydsl.core.types.Expression<Long> membersId, com.querydsl.core.types.Expression<String> membersNickname, com.querydsl.core.types.Expression<String> membersPicture, com.querydsl.core.types.Expression<com.hckst.respal.converter.TFCode> modifyYn, com.querydsl.core.types.Expression<java.time.LocalDateTime> regTime, com.querydsl.core.types.Expression<java.time.LocalDateTime> modifyTime) {
        super(ResumeDetailResponseDto.class, new Class<?>[]{long.class, String.class, int.class, long.class, String.class, String.class, com.hckst.respal.converter.TFCode.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class}, resumeId, title, views, membersId, membersNickname, membersPicture, modifyYn, regTime, modifyTime);
    }

}

