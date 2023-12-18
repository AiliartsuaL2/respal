package com.hckst.respal.resume.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.NumberExpression;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.hckst.respal.resume.domain.QResume.resume;

@Getter
@AllArgsConstructor
// Hub List 정렬조건
public enum ResumeSort{
    // 최신순
    RECENT_DESC(resume.id,Order.DESC),
    // 오래된순
    RECENT_ASC(resume.id,Order.ASC),
    // 조회수 높은순
    VIEWS_DESC(resume.views,Order.DESC),
    // 조회수 낮은순
    VIEWS_ASC(resume.views,Order.ASC);

    private final NumberExpression<?> sortCondition;
    private final Order order;
}
