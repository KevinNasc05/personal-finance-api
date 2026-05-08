package com.project.finance.api.dto.request;

import com.project.finance.api.model.enums.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequestDto(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 12, fraction = 2, message = "Amount must have up to 12 digits and 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Transaction type is required")
        TransactionType type,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        @PastOrPresent(message = "Transaction date cannot be in the future")
        @NotNull(message = "Date is required")
        LocalDate date

) {
}
