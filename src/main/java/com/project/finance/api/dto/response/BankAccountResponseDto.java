package com.project.finance.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BankAccountResponseDto(
        BigDecimal balance,
        LocalDateTime updatedAt,
        LocalDateTime createdAt
) {
}
