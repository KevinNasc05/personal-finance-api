package com.project.finance.api.repository;

import com.project.finance.api.model.BankAccount;
import com.project.finance.api.model.enums.TransactionType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ba FROM BankAccount ba WHERE id = :id")
    Optional<BankAccount> findByIdForUpdate(UUID id);

    Optional<BankAccount> findByUserId(UUID userId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
              FROM Transaction t
             WHERE t.bankAccount.id = :id
               AND (:startDate IS NULL OR t.date >= :startDate)
               AND (:endDate   IS NULL OR t.date <= :endDate  )
               AND t.type = :transactionType
               AND t.reversedTransaction.id IS NULL
               AND NOT EXISTS ( SELECT 1 FROM Transaction rt WHERE rt.reversedTransaction.id = t.id )
            """)
    BigDecimal getTotal(UUID id, TransactionType transactionType, LocalDate startDate, LocalDate endDate);

}
