package com.project.finance.api.dto.response;

import java.math.BigDecimal;

public record BankAccountSummaryDto(
        BigDecimal currentBalance,
        BigDecimal periodBalance,
        BigDecimal totalIncome,
        BigDecimal totalExpense
) {
}
