package com.hckst.respal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST,"요청 형식이 잘못 되었어요.","R000"),
    // 0번대 JWT 관련 오류
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"만료된 토큰 정보에요.","R001"),
    INCORRECT_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"일치하지 않는 Refresh Token 이에요.","R002"),
    MALFORMED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"지원하지 않는 토큰이에요.","R003"),
    NOT_EXIST_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"존재하지 않는 Refresh Token 이에요","R004"),
    NOT_EXIST_TOKEN_INFO_EXCEPTION(HttpStatus.FORBIDDEN,"토큰 정보가 존재하지 않아요.","R005"),
    SIGNATURE_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"잘못된 토큰 정보에요.","R006"),

    // 100번대 OAuth 관련 오류
    NO_SUCH_OAUTH_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"OAuth Code값이 존재하지 않아요.","R101"),
    NO_SUCH_OAUTH_TMP_UID_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 UID에 해당하는 값이 존재하지 않아요.","R102"),
    INCORRECT_OAUTH_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST,"OAuth type 설정이 잘못되었어요.","R103"),

    // 201번대 Members 관련 오류
    DUPLICATE_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일이에요.","R201"),
    NOT_EXIST_PASSWORD_EXCEPTION(HttpStatus.BAD_REQUEST,"일반 회원은 비밀번호 설정이 필수에요.","R201"),
    INVALID_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST,"유효하지 않은 사용자에요.","R201"),
    INCORRECT_MAIL_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST,"메일 발송 조건이 충족되지 않았어요.","R201"),
    NOT_EXIST_PROVIDER_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST,"Provider 타입이 존재하지 않아요.","R201"),
    NOT_EXIST_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 회원이 존재하지 않아요.","R201"),
    PERMISSION_DENIED_TO_CREATE_EXCEPTION(HttpStatus.BAD_REQUEST,"추가 권한이 없어요.","R201"),
    PERMISSION_DENIED_TO_DELETE_EXCEPTION(HttpStatus.BAD_REQUEST,"삭제 권한이 없어요.","R201"),

    // 202번대 Resume 관련 오류
    NOT_EXIST_RESUME_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 이력서가 존재하지 않아요.","R202"),
    NOT_EXIST_RESUME_FILE_ID_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 이력서 파일이 존재하지 않아요.","R202"),
    FAILED_FILE_UPLOAD_TO_S3_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"파일 업로드 중 문제가 발생했어요.","R202"),
    FAILED_FILE_DELETE_TO_S3_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"파일 삭제 중 문제가 발생했어요.","R202"),
    NOT_EXIST_RESUME_TYPE(HttpStatus.BAD_REQUEST,"이력서 공개 설정이 지정되지 않았어요.","R202"),
    PERMISSION_DENIED_TO_VIEW_EXCEPTION(HttpStatus.FORBIDDEN, "조회 권한이 없어요", "R202"),

    // 203번대 Comment 관련 오류
    NOT_EXIST_COMMENT_EXCEPTION(HttpStatus.BAD_REQUEST,"존재하지 않는 댓글이에요.","R203"),
    ILLEGAL_COMMENT_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST, "댓글 내용을 입력해주세요", "R203"),

    // 204번대 Tag 관련 오류
    PERMISSION_DENIED_TO_TAG_EXCEPTION(HttpStatus.BAD_REQUEST,"태그 권한이 없어요", "R204"),
    CAN_NOT_TAG_PUBLIC_RESUME_EXCEPTION(HttpStatus.BAD_REQUEST,"공개된 이력서에서는 태그를 할 수 없어요","R204"),
    CAN_NOT_TAG_ONESELF_EXCEPTION(HttpStatus.BAD_REQUEST,"자기 자신은 태그 할 수 없어요.","R204"),
    NOT_EXIST_MEMBER_LIST_FOR_TAG_EXCEPTION(HttpStatus.BAD_REQUEST,"태그하려는 회원 리스트가 존재하지 않아요.","R204"),
    NOT_EXIST_TAG_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 태그가 존재하지 않아요.","R204");


    private final String msg;
    private final HttpStatus httpStatus;
    private final int code;
    private final String errorCode;
    ErrorMessage(HttpStatus httpStatus, String msg, String errorCode){
        this.msg = msg;
        this. httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.errorCode = errorCode;
    }
}
