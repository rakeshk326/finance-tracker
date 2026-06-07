package com.personal.finance_tracker.specification;

import com.personal.finance_tracker.entity.Transaction;
import com.personal.finance_tracker.enums.TransactionType;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> hasUserId(UUID userId) {
        return (root, query, cb) -> {
            root.fetch("category", JoinType.LEFT);
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<Transaction> belongsToAccount(UUID accountId) {
        return (root, query, cb) ->
                cb.equal(root.get("account").get("id"), accountId);
    }

    public static Specification<Transaction> hasType(TransactionType type) {
        return (root, query, cb) ->
                cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> betweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) ->
                cb.between(root.get("date"), startDate, endDate);
    }

    public static Specification<Transaction> hasCategoryId(UUID categoryId) {
        return (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Transaction> isNotDeleted() {
        return (root, query, cb) ->
                cb.isNull(root.get("deletedAt"));
    }
}