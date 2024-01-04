package com.hckst.respal.members.domain.repository;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.domain.repository.dto.QMembersOAuthDto;
import com.hckst.respal.members.presentation.dto.response.MembersResponseDto;
import com.hckst.respal.members.presentation.dto.request.SearchMembersRequestDto;
import com.hckst.respal.members.presentation.dto.response.QMembersResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<Members> findMembersAndResumeById(long membersId) {
        Members member = queryFactory.select(members)
                .from(members)
                .leftJoin(members.resumeList, resume).fetchJoin()
                .where(members.id.eq(membersId))
                .fetchOne();
        return Optional.ofNullable(member);
    }

    @Override
    public List<MembersResponseDto> findMembersByNickname(SearchMembersRequestDto searchMembersRequestDto) {
        return queryFactory.select(new QMembersResponseDto(
                members.id,
                members.nickname,
                members.picture,
                members.email
                )).from(members)
                // queryDSL 의 like 메서드는 파라미터 내부의 스트링 자체가 나가기 때문에 %를 붙여줘야함
                .where(members.nickname.like(searchMembersRequestDto.getSearchWord()+"%"))
                .limit(searchMembersRequestDto.getLimit())
                .offset(0)
                .fetch();
    }
}
