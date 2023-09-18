package com.hckst.respal.tag.presentation.dto.request;

import com.hckst.respal.members.domain.Members;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTagRequestDto {
    // 태그하려는 이력서의 주인
    @NotNull(message = "게시물 작성자는 필수 입력값이에요")
    private Members members;
    // 태그하려는 이력서의 id
    @NotNull(message = "태그하려는 이력서 ID는 필수 입력값이에요")
    private Long resumeId;
    // 태그당하는 회원의 id
    @NotNull(message = "태그하려는 회원 ID는 필수 입력값이에요 (jsonArray) ")
    private List<Long> membersIdList;
}
