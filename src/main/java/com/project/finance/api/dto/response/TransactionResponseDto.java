package com.project.finance.api.dto.response;

import com.project.finance.api.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponseDto(
        Long id,
        String description,
        BigDecimal amount,
        TransactionType type,
        CategoryResponseDto category,
        LocalDate date,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
