package com.hckst.respal.authentication.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtErrorResponseHandler {
    private final ObjectMapper objectMapper;

    public void generateJwtErrorResponse(HttpServletResponse response, ErrorMessage errorMessage) throws IOException {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(errorMessage);
        response.setStatus(errorMessage.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
    }
}
