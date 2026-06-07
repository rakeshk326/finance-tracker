package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.budget.CreateBudgetRequestDTO;
import com.personal.finance_tracker.dto.budget.BudgetResponseDTO;
import com.personal.finance_tracker.dto.budget.UpdateBudgetRequestDTO;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface BudgetService {

    BudgetResponseDTO createBudget(@Valid CreateBudgetRequestDTO createBudgetRequestDTO);

    BudgetResponseDTO updateBudget(UUID id, UpdateBudgetRequestDTO updateBudgetRequestDTO);

    void deleteBudget(UUID id);

    List<BudgetResponseDTO> getBudgets(int month, int year);
}
