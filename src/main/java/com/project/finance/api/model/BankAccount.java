package com.project.finance.api.model;

import com.project.finance.api.exceptions.BusinessException;
import com.project.finance.api.exceptions.ConflictException;
import com.project.finance.api.model.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_accounts")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public BankAccount(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        this.user = user;
    }

    public void apply(Transaction transaction) {
        if (transaction.getType() == TransactionType.EXPENSE) {
            this.debit(transaction.getAmount());
            return;
        }
        this.credit(transaction.getAmount());
    }

    private void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new ConflictException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    private void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

}
