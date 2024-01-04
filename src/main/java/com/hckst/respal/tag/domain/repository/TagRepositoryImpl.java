package com.hckst.respal.tag.domain.repository;

import com.hckst.respal.tag.domain.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import static com.hckst.respal.resume.domain.QResume.resume;
import static com.hckst.respal.tag.domain.QTag.tag;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Tag> findTagAndResumeByMembersId(Long membersId) {
        Tag findedTag = queryFactory.select(tag)
                .from(tag)
                .join(tag.resume, resume).fetchJoin()
                .where(tag.members.id.eq(membersId))
                .fetchOne();
        return Optional.ofNullable(findedTag);
    }
}
