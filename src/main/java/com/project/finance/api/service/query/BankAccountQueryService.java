package com.project.finance.api.service.query;

import com.project.finance.api.dto.response.BankAccountSummaryDto;
import com.project.finance.api.exceptions.BusinessException;
import com.project.finance.api.exceptions.NotFoundException;
import com.project.finance.api.model.BankAccount;
import com.project.finance.api.model.enums.TransactionType;
import com.project.finance.api.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    public class BankAccountQueryService {

        private final BankAccountRepository bankAccountRepository;

        @Transactional
        public BankAccount findByIdForUpdate(UUID id) {
            return bankAccountRepository.findByIdForUpdate(id)
                    .orElseThrow( () -> new NotFoundException("Bank Account not found") );
        }

        @Transactional(readOnly = true)
        public BankAccount findByUserId(UUID userId) {
            return bankAccountRepository.findByUserId(userId)
                    .orElseThrow( () -> new NotFoundException("Bank account not found") );
        }

        @Transactional(readOnly = true)
        public BankAccountSummaryDto getSummary(UUID userId, LocalDate startDate, LocalDate endDate) {
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new BusinessException("Start date cannot be after end date");
            }
            BankAccount account = findByUserId(userId);

            BigDecimal totalIncome = bankAccountRepository.getTotal(account.getId(), TransactionType.INCOME, startDate, endDate);
            BigDecimal totalExpense = bankAccountRepository.getTotal(account.getId(), TransactionType.EXPENSE, startDate, endDate);

            return new BankAccountSummaryDto(
                    account.getBalance(),
                    totalIncome.subtract(totalExpense),
                    totalIncome,
                    totalExpense
            );
        }

    }
