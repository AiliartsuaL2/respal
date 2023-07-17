package com.hckst.respal.members.domain.repository;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.domain.repository.dto.QMembersOAuthDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hckst.respal.comment.domain.QComment.comment;
import static com.hckst.respal.authentication.oauth.domain.QOauth.oauth;
import static com.hckst.respal.members.domain.QMembers.members;
import static com.hckst.respal.resume.domain.QResume.resume;

@Repository
@RequiredArgsConstructor
public class MembersRepositoryImpl implements MembersRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Optional<MembersOAuthDto> findMembersOauthForLogin(String email, Provider provider) {
        MembersOAuthDto member = queryFactory.select(new QMembersOAuthDto(
                members.id,
                members.email
                )).from(members)
                .leftJoin(members.oauthList,oauth)
                .where(members.email.eq(email)
                        .and(oauth.provider.eq(provider)))
                .fetchOne();
        return Optional.ofNullable(member);
    }

    @Override
    public Boolean existsMembersOauthForJoin(String email, Provider provider) {
        return queryFactory.from(members)
                .leftJoin(members.oauthList, oauth)
                .where(members.email.eq(email)
                        .and(oauth.provider.eq(provider).or(oauth.provider.isNull())))
                .fetchFirst() != null;
    }

    @Override
    public Optional<Members> findCommonMembersByEmail(String email) {
        Members member = queryFactory.select(members)
                .from(members)
                .leftJoin(members.oauthList, oauth)
                .where(members.email.eq(email)
                        .and(oauth.provider.isNull())
                )
                .fetchOne();
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Members> findMembersAndCommentById(long membersId) {
        Members member = queryFactory.select(members)
                .from(members)
                .leftJoin(members.commentList, comment).fetchJoin()
                .where(members.id.eq(membersId))
                .fetchOne();
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Members> findMembersAndResumeById(long membersId) {
        Members member = queryFactory.select(members)
                .from(members)
                .leftJoin(members.resumeList, resume).fetchJoin()
                .where(members.id.eq(membersId))
                .fetchOne();
        return Optional.ofNullable(member);
    }


}
