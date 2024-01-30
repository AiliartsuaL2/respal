package com.hckst.respal.members.presentation.dto.request;

import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.CommonRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "일반 이메일 로그인 요청")
public class MembersLoginRequestDto extends CommonRequestDto {
    @Schema(description = "이메일", nullable = false, example = "abc@jiniworld.me")
    private String email;
    @Schema(description = "비밀번호", nullable = false)
    private String password;

    @Override
    public void checkRequiredFieldIsNull() {
        if(ObjectUtils.isEmpty(this.email)) {
            throw new IllegalArgumentException(ErrorMessage.NOT_EXIST_MEMBER_EMAIL_EXCEPTION.getMsg());
        }
        if(ObjectUtils.isEmpty(this.password)) {
            throw new IllegalArgumentException(ErrorMessage.NOT_EXIST_MEMBER_PASSWORD_EXCEPTION.getMsg());
        }
    }
}
