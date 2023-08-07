package com.hckst.respal.mention.presentation.dto.request;

import com.hckst.respal.members.domain.Members;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMentionRequestDto {
    // 멘션하려는 이력서의 주인
    private Members members;
    // 멘션하려는 이력서의 id
    private Long resumeId;
    // 멘션당하는 회원의 id
    private Long membersId;
}
