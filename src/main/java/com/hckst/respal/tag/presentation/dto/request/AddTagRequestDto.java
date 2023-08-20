package com.hckst.respal.tag.presentation.dto.request;

import com.hckst.respal.members.domain.Members;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTagRequestDto {
    // 태그하려는 이력서의 주인
    private Members members;
    // 태그하려는 이력서의 id
    private Long resumeId;
    // 태그당하는 회원의 id
    private List<Long> membersIdList;
}
