package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.budget.CreateBudgetRequestDTO;
import com.personal.finance_tracker.dto.budget.BudgetResponseDTO;
import com.personal.finance_tracker.dto.budget.UpdateBudgetRequestDTO;
import com.personal.finance_tracker.entity.Budget;
import com.personal.finance_tracker.entity.Category;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.enums.TransactionType;
import com.personal.finance_tracker.exception.ResourceAlreadyExistsException;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.mapper.BudgetMapper;
import com.personal.finance_tracker.repository.BudgetRepository;
import com.personal.finance_tracker.repository.CategoryRepository;
import com.personal.finance_tracker.repository.TransactionRepository;
import com.personal.finance_tracker.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    BudgetRepository budgetRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    BudgetMapper budgetMapper;

    @Override
    public List<BudgetResponseDTO> getBudgets(int month, int year) {

        UUID userId = SecurityUtil.getCurrentUserId();

        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYearAndDeletedAtIsNull(userId, month, year);

        return budgets.stream().map(budget -> {
            BudgetResponseDTO budgetResponseDTO = budgetMapper.toBudgetResponseDTO(budget);

            BigDecimal spentAmount = transactionRepository.calculateTotalSpentForBudget(
                    userId,
                    budget.getCategory().getId(),
                    TransactionType.EXPENSE,
                    budget.getMonth(),
                    budget.getYear()
            );

            budgetResponseDTO.setSpentAmount(spentAmount);
            return budgetResponseDTO;
        }).toList();

    }

    @Override
    @Transactional
    public BudgetResponseDTO createBudget(CreateBudgetRequestDTO createBudgetRequestDTO) {

        UUID userId = SecurityUtil.getCurrentUserId();
        User user = new User();
        user.setId(userId);

        Category category = categoryRepository.findById(createBudgetRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        boolean budgetExists = budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYearAndDeletedAtIsNull(
                userId, category.getId(), createBudgetRequestDTO.getMonth(), createBudgetRequestDTO.getYear()
        );

        if (budgetExists) {
            throw new ResourceAlreadyExistsException("A budget for this category already exists for the specified month and year.");
        }

        Budget budget = budgetMapper.toEntity(createBudgetRequestDTO);
        budget.setUser(user);
        budget.setCategory(category);

        Budget savedBudget = budgetRepository.save(budget);
        BudgetResponseDTO responseDTO = budgetMapper.toBudgetResponseDTO(savedBudget);

        BigDecimal spentAmount = transactionRepository.calculateTotalSpentForBudget(
                userId, savedBudget.getCategory().getId(), TransactionType.EXPENSE, savedBudget.getMonth(), savedBudget.getYear()
        );

        responseDTO.setSpentAmount(spentAmount);
        return responseDTO;
    }

    @Override
    @Transactional
    public BudgetResponseDTO updateBudget(UUID budgetId, UpdateBudgetRequestDTO updateBudgetRequestDTO) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Budget budget = budgetRepository.findByIdAndUserIdAndDeletedAtIsNull(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found or access denied"));

        int targetMonth = updateBudgetRequestDTO.getMonth() != null ?
                updateBudgetRequestDTO.getMonth() : budget.getMonth();

        int targetYear = updateBudgetRequestDTO.getYear() != null ?
                updateBudgetRequestDTO.getYear() : budget.getYear();

        boolean isUniqueIdentifiersChanging = !budget.getCategory().getId().equals(updateBudgetRequestDTO.getCategoryId()) || budget.getMonth() != targetMonth || budget.getYear() != targetYear;

        if (isUniqueIdentifiersChanging) {

            boolean collision = budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYearAndDeletedAtIsNull(
                    userId, updateBudgetRequestDTO.getCategoryId(), targetMonth, targetYear
            );

            if (collision) {
                throw new ResourceAlreadyExistsException("Cannot update: A budget for this category, month, and year already exists.");
            }

            if (updateBudgetRequestDTO.getCategoryId() != null) {
                Category newCategory = categoryRepository.findByIdAndDeletedAtIsNull(updateBudgetRequestDTO.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("New category not found"));
                budget.setCategory(newCategory);
            }

            budget.setMonth(targetMonth);
            budget.setYear(targetYear);

        }

        if (updateBudgetRequestDTO.getLimitAmount() != null) {
            budget.setLimitAmount(updateBudgetRequestDTO.getLimitAmount());
        }

        Budget savedBudget = budgetRepository.save(budget);
        BudgetResponseDTO responseDTO = budgetMapper.toBudgetResponseDTO(savedBudget);

        BigDecimal spentAmount = transactionRepository.calculateTotalSpentForBudget(
                userId, savedBudget.getCategory().getId(), TransactionType.EXPENSE, savedBudget.getMonth(), savedBudget.getYear()
        );

        responseDTO.setSpentAmount(spentAmount);
        return responseDTO;
    }

    @Override
    @Transactional
    public void deleteBudget(UUID budgetId) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Budget budget = budgetRepository.findByIdAndUserIdAndDeletedAtIsNull(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found or access denied"));

        budget.setDeletedAt(LocalDateTime.now());
        budgetRepository.save(budget);
    }
}
