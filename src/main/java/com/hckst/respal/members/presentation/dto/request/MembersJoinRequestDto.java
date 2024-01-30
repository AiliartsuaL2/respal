package com.hckst.respal.members.presentation.dto.request;

import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.CommonRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "일반 이메일 회원 가입 요청")
public class MembersJoinRequestDto extends CommonRequestDto {
    @Schema(description = "이메일", nullable = false, example = "abc@jiniworld.me")
    @Email(message = "이메일 형식에 맞지 않아요. /")
    @NotNull(message = "이메일은 필수 입력 항목이에요")
    private String email;

    @Schema(description = "비밀번호")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()\\-_=+\\\\|\\[\\]{};:'\",<.>/?]).{8,16}$",
            message = "비밀번호는 영문 , 숫자, 특수문자를 각각 1개 이상 포함하고, 8자리 이상 16자리 이하여야해요.")
    private String password;

    @Schema(description = "닉네임", nullable = false)
    @NotNull(message = "회원가입 필수 입력 조건이 충족되지 않았어요. /")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{2,16}$",
            message = "닉네임은 특수문자를 제외한 2자이상 16자이하의 문자열만 입력이 가능해요.")
    private String nickname;

    @Schema(description = "사진")
    private String picture;

    @Schema(description = "provider", nullable = false, allowableValues = {"common","kakao","google","github"})
    @NotNull(message = "Provider는 필수 입력 항목이에요")
    private String provider;

    @Override
    public void checkRequiredFieldIsNull() {
        checkNull(this.email, ErrorMessage.NOT_EXIST_MEMBER_EMAIL_EXCEPTION);
        checkNull(this.password, ErrorMessage.NOT_EXIST_PASSWORD_EXCEPTION);
        checkNull(this.nickname, ErrorMessage.NOT_EXIST_NICKNAME_EXCEPTION);
        checkNull(this.provider, ErrorMessage.NOT_EXIST_PROVIDER_TYPE_EXCEPTION);
    }
}
