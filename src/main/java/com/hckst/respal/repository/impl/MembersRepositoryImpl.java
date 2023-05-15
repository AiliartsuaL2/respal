package com.hckst.respal.repository.impl;

import com.hckst.respal.common.converter.Provider;
import com.hckst.respal.domain.Members;
import com.hckst.respal.domain.QMembers;
import com.hckst.respal.repository.custom.MembersRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.hckst.respal.domain.QMembers.members;
import static com.hckst.respal.domain.QOauth.oauth;

@Repository
@RequiredArgsConstructor
public class MembersRepositoryImpl implements MembersRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Optional<Members> findMembersOauth(String email, Provider provider) {
        Members member = queryFactory.selectFrom(QMembers.members)
                .join(members.oauthList,oauth)
                .where(members.email.eq(email)
                        .and(oauth.provider.eq(provider)))
                .fetchOne();
        return Optional.ofNullable(member);
    }

}
