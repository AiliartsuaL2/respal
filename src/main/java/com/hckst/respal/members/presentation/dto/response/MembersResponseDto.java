package com.hckst.respal.members.presentation.dto.response;

import com.hckst.respal.members.domain.Members;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class MembersResponseDto {
    private Long membersId;
    private String nickname;
    private String picture;
    private String email;

    @QueryProjection
    public MembersResponseDto(Long membersId, String nickname, String picture, String email) {
        this.membersId = membersId;
        this.nickname = nickname;
        this.picture = picture;
        this.email = email;
    }

    public static MembersResponseDto convert(Members members) {
        return MembersResponseDto.builder()
                .membersId(members.getId())
                .nickname(members.getNickname())
                .picture(members.getPicture())
                .email(members.getEmail())
                .build();
    }
}
