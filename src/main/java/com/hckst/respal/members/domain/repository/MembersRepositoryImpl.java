package com.hckst.respal.members.domain.repository;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.Members;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.hckst.respal.members.domain.QMembers.members;
import static com.hckst.respal.authentication.oauth.domain.QOauth.oauth;

@Repository
@RequiredArgsConstructor
public class MembersRepositoryImpl implements MembersRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Optional<Members> findMembersOauth(String email, Provider provider) {
        Members member = queryFactory.selectFrom(members)
                .join(members.oauthList,oauth)
                .where(members.email.eq(email)
                        .and(oauth.provider.eq(provider)))
                .fetchOne();
        return Optional.ofNullable(member);
    }

}
