package com.project.finance.api.security;

import com.project.finance.api.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        int httpStatusNumber = HttpStatus.UNAUTHORIZED.value();

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                httpStatusNumber,
                "UNAUTHORIZED",
                "Invalid credentials",
                request.getRequestURI()
        );

        response.setStatus(httpStatusNumber);
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), error);
    }

}
