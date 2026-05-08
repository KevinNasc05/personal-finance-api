package com.project.finance.api.exceptions;

import com.project.finance.api.dto.response.ErrorResponse;
import com.project.finance.api.dto.response.FieldErrorResponseDto;
import com.project.finance.api.dto.response.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<FieldErrorResponseDto> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorResponseDto(
                        error.getField(),
                        error.getDefaultMessage())
                ).toList();

        return ResponseEntity
                .badRequest()
                .body(
                        new ValidationErrorResponse(
                                LocalDateTime.now(),
                                exception.getStatusCode().value(),
                                "VALIDATION_ERROR",
                                "Invalid fields",
                                request.getRequestURI(),
                                fieldErrors
                        )
                );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                "NOT_FOUND",
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException exception, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "BUSINESS_ERROR",
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException exception, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                "CONFLICT_ERROR",
                                exception.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "INTERNAL_SERVER_ERROR",
                                "Unexpected error",
                                request.getRequestURI()
                        )
                );
    }

}
