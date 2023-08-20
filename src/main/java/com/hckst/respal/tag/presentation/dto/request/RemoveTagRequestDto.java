package com.hckst.respal.tag.presentation.dto.request;

import com.hckst.respal.members.domain.Members;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveTagRequestDto {
    // 삭제하고싶은 멘션의 id
    private Long tagId;
    // 해당 멘션 삭제의 주체 (이력서의 주인 or 멘션 당한사람)
    private Members members;

}
