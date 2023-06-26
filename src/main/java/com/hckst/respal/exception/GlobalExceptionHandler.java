package com.hckst.respal.exception;

import com.hckst.respal.global.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
                .body(new ApiErrorResponse(statusCode,ex.getMessage(),ex.getErrorCode()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> RuntimeException(RuntimeException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .statusCode(400)
                .message(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> MethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        BindingResult bindingResult = ex.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }
        String message = builder.toString();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .statusCode(400)
                .message(message)
                .errorCode("R")
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}