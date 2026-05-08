package com.project.finance.api.repository.specs;

import com.project.finance.api.model.Transaction;
import com.project.finance.api.model.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class TransactionSpecs {

    public static Specification<Transaction> accountIdEqual(UUID accountId) {
        if (accountId == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("bankAccount").get("id"), accountId);
    }

    public static Specification<Transaction> dateGreaterOrEqual(LocalDate start) {
        if (start == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), start);
    }

    public static Specification<Transaction> dateLessOrEqual(LocalDate end) {
        if (end == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), end);
    }

    public static Specification<Transaction> typeEqual(TransactionType type) {
        if (type == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> categoryIdEqual(Long categoryId) {
        if (categoryId == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

}
