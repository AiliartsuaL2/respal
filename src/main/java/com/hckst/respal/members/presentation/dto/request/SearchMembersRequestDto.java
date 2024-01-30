package com.hckst.respal.members.presentation.dto.request;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.CommonRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMembersRequestDto extends CommonRequestDto {
    private String searchWord;
    // 웹용과 모바일용은 리스트업 개수를 다르게 해야하기 때문에?
    private int limit;

    @Override
    public void checkRequiredFieldIsNull() {
        checkNull(searchWord, ErrorMessage.NOT_EXIST_MEMBER_EMAIL_EXCEPTION);
    }
}
