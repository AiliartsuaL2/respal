package com.hckst.respal.members.presentation.dto.request;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class SearchMembersResponseDto {
    private Long membersId;
    private String nickname;
    private String picture;
    private String email;

    @QueryProjection
    public SearchMembersResponseDto(Long membersId, String nickname, String picture, String email) {
        this.membersId = membersId;
        this.nickname = nickname;
        this.picture = picture;
        this.email = email;
    }
}
