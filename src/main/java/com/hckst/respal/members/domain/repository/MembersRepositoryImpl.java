package com.hckst.respal.members.domain.repository;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.QMembers;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.domain.repository.dto.QMembersOAuthDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hckst.respal.members.domain.QMembers.members;
import static com.hckst.respal.authentication.oauth.domain.QOauth.oauth;

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
        Members member = queryFactory.selectFrom(QMembers.members)
                .leftJoin(QMembers.members.oauthList, oauth)
                .where(QMembers.members.email.eq(email)
                        .and(oauth.provider.isNull())
                ).fetchOne();
        return Optional.ofNullable(member);
    }

}
