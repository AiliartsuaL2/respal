package com.hckst.respal.mention.domain.repository;

import com.hckst.respal.mention.domain.Mention;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import static com.hckst.respal.mention.domain.QMention.mention;
import static com.hckst.respal.resume.domain.QResume.resume;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MentionRepositoryImpl implements MentionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Mention> findMentionAndResumeById(Long mentionId) {
        Mention findedMention = queryFactory.select(mention)
                .from(mention)
                .join(mention.resume, resume).fetchJoin()
                .where(mention.id.eq(mentionId))
                .fetchOne();
        return Optional.ofNullable(findedMention);
    }
}
