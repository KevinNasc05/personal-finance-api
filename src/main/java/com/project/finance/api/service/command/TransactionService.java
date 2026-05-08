package com.project.finance.api.service.command;

import com.project.finance.api.dto.request.TransactionRequestDto;
import com.project.finance.api.exceptions.BusinessException;
import com.project.finance.api.exceptions.ConflictException;
import com.project.finance.api.exceptions.NotFoundException;
import com.project.finance.api.mappers.TransactionMapper;
import com.project.finance.api.model.BankAccount;
import com.project.finance.api.model.Category;
import com.project.finance.api.model.Transaction;
import com.project.finance.api.model.User;
import com.project.finance.api.model.enums.TransactionType;
import com.project.finance.api.repository.TransactionRepository;
import com.project.finance.api.service.query.BankAccountQueryService;
import com.project.finance.api.service.query.CategoryQueryService;
import com.project.finance.api.service.query.TransactionQueryService;
import com.project.finance.api.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static com.project.finance.api.model.enums.TransactionType.EXPENSE;
import static com.project.finance.api.model.enums.TransactionType.INCOME;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionQueryService transactionQueryService;
    private final TransactionMapper transactionMapper;
    private final BankAccountQueryService bankAccountQueryService;
    private final CategoryQueryService categoryQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public Transaction save(UUID userId, TransactionRequestDto dto) {
        User userFound = userQueryService.findById(userId);
        BankAccount bankAccount = bankAccountQueryService.findByIdForUpdate(userFound.getBankAccount().getId());

        Category category = categoryQueryService.findByIdAndUserId(dto.categoryId(), userId);
        if (!category.getActive()) {
            throw new ConflictException("Category is inactive");
        }

        Transaction transaction = transactionMapper.toEntity(dto, category, bankAccount);
        bankAccount.apply(transaction);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction reverse(UUID userId, Long transactionId) {
        if (transactionRepository.existsByReversedTransaction_Id(transactionId)) {
            throw new ConflictException("Transaction already reversed");
        }

        Transaction originalTransaction = transactionQueryService.findById(userId, transactionId);
        BankAccount bankAccount = bankAccountQueryService.findByIdForUpdate(originalTransaction.getBankAccount().getId());

        TransactionType reverseTransactionType = originalTransaction.getType() == INCOME ? EXPENSE : INCOME;
        if (reverseTransactionType == EXPENSE && bankAccount.getBalance().compareTo(originalTransaction.getAmount()) < 0) {
            throw new ConflictException("Insufficient balance to reverse transaction");
        }

        Transaction reverseTransaction = new Transaction(
                bankAccount,
                "REVERSAL - Transaction " + originalTransaction.getId(),
                originalTransaction.getAmount(),
                reverseTransactionType,
                originalTransaction.getCategory(),
                LocalDate.now()
        );
        reverseTransaction.setReversedTransaction(originalTransaction);

        bankAccount.apply(reverseTransaction);
        return transactionRepository.save(reverseTransaction);
    }

}
