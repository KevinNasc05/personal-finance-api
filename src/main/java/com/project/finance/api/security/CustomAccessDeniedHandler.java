package com.project.finance.api.security;

import com.project.finance.api.dto.response.ErrorResponse;
import com.project.finance.api.model.enums.UserStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String message = "Access denied";
        int httpStatusNumber = HttpStatus.FORBIDDEN.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();

        if (principal instanceof CustomUserDetails user && user.getStatus() == UserStatus.INACTIVE) {
            message = "Your account is inactive. Reactivate your account to continue.";
        }

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                httpStatusNumber,
                "FORBIDDEN",
                message,
                request.getRequestURI()
        );

        response.setStatus(httpStatusNumber);
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), error);
    }

}
