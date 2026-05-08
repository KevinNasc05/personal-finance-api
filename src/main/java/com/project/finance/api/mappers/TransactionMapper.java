package com.project.finance.api.mappers;

import com.project.finance.api.dto.request.TransactionRequestDto;
import com.project.finance.api.dto.response.CategoryResponseDto;
import com.project.finance.api.dto.response.TransactionResponseDto;
import com.project.finance.api.model.BankAccount;
import com.project.finance.api.model.Category;
import com.project.finance.api.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequestDto dto, Category category, BankAccount bankAccount) {
        return new Transaction(
                bankAccount,
                dto.description(),
                dto.amount(),
                dto.type(),
                category,
                dto.date()
        );
    }

    public TransactionResponseDto toResponse(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                new CategoryResponseDto(
                        transaction.getCategory().getId(),
                        transaction.getCategory().getName()
                ),
                transaction.getDate(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

}
