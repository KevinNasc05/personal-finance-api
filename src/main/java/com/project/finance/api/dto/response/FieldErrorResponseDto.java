package com.project.finance.api.dto.response;

public record FieldErrorResponseDto(
        String field,
        String message
) {
}
