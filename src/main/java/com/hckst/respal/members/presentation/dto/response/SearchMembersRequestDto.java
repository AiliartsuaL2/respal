package com.hckst.respal.members.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMembersRequestDto {
    private String searchWord;
    // 웹용과 모바일용은 리스트업 개수를 다르게 해야하기 때문에?
    private int limit;

}
