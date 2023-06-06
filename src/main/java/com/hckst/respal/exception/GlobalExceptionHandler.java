package com.hckst.respal.exception;

import com.hckst.respal.exception.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@RestControllerAdvice // exception 스코프를 패키지 레벨로
public class GlobalExceptionHandler {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> applicationException(ApplicationException ex){
        int errorCode = ex.getErrorCode();
        log.warn(
                LOG_FORMAT,
                ex.getClass().getSimpleName(),
                errorCode,
                ex.getMessage()
        );
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new ApiErrorResponse(errorCode,ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> RuntimeException(RuntimeException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .errorCode(400)
                .message(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}