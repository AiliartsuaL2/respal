package com.hckst.respal.exception;

import com.hckst.respal.exception.oauth.OAuthLoginException;
import com.hckst.respal.global.dto.ApiErrorMessageAndCode;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.test.TestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // exception 스코프를 패키지 레벨로
public class GlobalExceptionHandler {
    private static final String LOG_FORMAT = "Class : {}, StatusCode : {}, Message : {} , ErrorCode : {}";
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> applicationException(ApplicationException ex){
        int statusCode = ex.getStatusCode();
        log.warn(
                LOG_FORMAT,
                ex.getClass().getSimpleName(),
                statusCode,
                ex.getMessage(),
                ex.getErrorCode()
        );
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiErrorResponse.builder()
                        .statusCode(statusCode)
                        .result(ApiErrorMessageAndCode.builder()
                                .errorCode(ex.getErrorCode())
                                .message(ex.getMessage())
                                .build())
                        .build());
    }

    @ExceptionHandler(TestException.class)
    public ResponseEntity<ApiErrorResponse> TestException(TestException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .statusCode(500)
                .result(ApiErrorMessageAndCode.builder()
                        .message(message)
                        .build())
                .build();
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> RuntimeException(RuntimeException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .statusCode(400)
                .result(ApiErrorMessageAndCode.builder()
                                .message(message)
                                .build())
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> MethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();

        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getFieldErrors().get(0).getDefaultMessage();

        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .statusCode(400)
                .result(ApiErrorMessageAndCode.builder()
                                .errorCode("R000")
                                .message(message)
                                .build())
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
    // 로그인 실패시 회원가입창으로 이동
    @ExceptionHandler(OAuthLoginException.class)
    public ResponseEntity<ApiErrorResponse> oauthLoginException(OAuthLoginException ex){
        int statusCode = ex.getStatusCode();
        log.warn(
                LOG_FORMAT,
                ex.getClass().getSimpleName(),
                statusCode,
                ex.getMessage(),
                ex.getErrorCode()
        );
        return ResponseEntity.status(HttpStatus.FOUND).location(ex.getRedirectUrl()).build();
    }
}
