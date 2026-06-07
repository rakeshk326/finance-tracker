package com.personal.finance_tracker.repository;

import com.personal.finance_tracker.entity.Transaction;
import com.personal.finance_tracker.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    @Query(value = """
        SELECT t 
        FROM Transaction t
        JOIN FETCH t.category
        WHERE t.user.id = :userId
        ORDER BY t.date DESC
    """)
    List<Transaction> findByUserIdWithCategoryOrderByDateDesc(UUID userId);

    Optional<Transaction> findByIdAndDeletedAtIsNull(UUID transactionId);

    List<Transaction> findByReferenceIdAndDeletedAtIsNull(UUID referenceId);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) 
        FROM Transaction t 
        WHERE t.user.id = :userId 
          AND t.category.id = :categoryId 
          AND t.type = :transactionType
          AND EXTRACT(MONTH FROM t.date) = :month 
          AND EXTRACT(YEAR FROM t.date) = :year 
          AND t.deletedAt IS NULL
    """)
    BigDecimal calculateTotalSpentForBudget(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("transactionType") TransactionType transactionType,
            @Param("month") int month,
            @Param("year") int year
    );
}
