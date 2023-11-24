package com.hckst.respal.authentication.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.ApiErrorMessageAndCode;
import com.hckst.respal.global.dto.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        setResponse(response,ErrorMessage.NOT_EXIST_TOKEN_INFO_EXCEPTION);
    }
    private void setResponse( HttpServletResponse response, ErrorMessage errorMessage) throws IOException {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .statusCode(errorMessage.getCode())
                .result(ApiErrorMessageAndCode.builder()
                        .errorCode(errorMessage.getErrorCode())
                        .message(errorMessage.getMsg())
                        .build())
                .build();
        response.setStatus(errorMessage.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
    }
}
