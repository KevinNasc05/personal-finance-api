package com.project.finance.api.service.query;

import com.project.finance.api.dto.response.PageResponseDto;
import com.project.finance.api.dto.response.TransactionResponseDto;
import com.project.finance.api.exceptions.BusinessException;
import com.project.finance.api.exceptions.NotFoundException;
import com.project.finance.api.mappers.TransactionMapper;
import com.project.finance.api.model.Transaction;
import com.project.finance.api.model.User;
import com.project.finance.api.model.enums.TransactionType;
import com.project.finance.api.repository.TransactionRepository;
import com.project.finance.api.repository.specs.TransactionSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionQueryService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserQueryService userQueryService;

    @Transactional(readOnly = true)
    public Transaction findById(UUID userId, Long transactionId) {
        Transaction transactionFound = transactionRepository.findById(transactionId)
                .orElseThrow( () -> new NotFoundException("Transaction not found") );
        if (!userQueryService.findById(userId).getBankAccount().equals(transactionFound.getBankAccount())) {
            throw new NotFoundException("Transaction not found");
        }
        return transactionFound;
    }

    @Transactional(readOnly = true)
    public PageResponseDto<TransactionResponseDto> filter(
            UUID userId, LocalDate start, LocalDate end, TransactionType type, Long categoryId, Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            throw new BusinessException("Page size too large");
        }
        if (start != null && end != null && start.isAfter(end)) {
            throw new BusinessException("Start date cannot be after end date");
        }

        User userFound = userQueryService.findById(userId);
        Specification<Transaction> spec = Specification
                .where(TransactionSpecs.accountIdEqual(userFound.getBankAccount().getId()))
                .and(TransactionSpecs.dateGreaterOrEqual(start))
                .and(TransactionSpecs.dateLessOrEqual(end))
                .and(TransactionSpecs.typeEqual(type))
                .and(TransactionSpecs.categoryIdEqual(categoryId));
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        return new PageResponseDto<TransactionResponseDto>(
                transactionPage.getContent().stream()
                        .map(transactionMapper::toResponse)
                        .toList(),
                transactionPage.getNumber(),
                transactionPage.getSize(),
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                transactionPage.isFirst(),
                transactionPage.isLast()
        );
    }

}
