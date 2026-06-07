package com.personal.finance_tracker.repository;

import com.personal.finance_tracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    boolean existsByUserIdAndCategoryIdAndMonthAndYearAndDeletedAtIsNull(UUID userId, UUID id, int month, int year);

    Optional<Budget> findByIdAndUserIdAndDeletedAtIsNull(UUID budgetId, UUID userId);

    List<Budget> findByUserIdAndMonthAndYearAndDeletedAtIsNull(UUID userId, int month, int year);
}
