package com.hckst.respal.authentication.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.global.dto.ApiErrorMessageAndCode;
import com.hckst.respal.global.dto.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        setResponse(response,"권한이 없습니다.");
    }

    private void setResponse( HttpServletResponse response, String message) throws IOException {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .result(ApiErrorMessageAndCode.builder()
                        .message(message)
                        .build())
                .build();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
    }
}
