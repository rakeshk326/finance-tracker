package com.personal.finance_tracker.controller;

import com.personal.finance_tracker.dto.budget.CreateBudgetRequestDTO;
import com.personal.finance_tracker.dto.budget.BudgetResponseDTO;
import com.personal.finance_tracker.dto.budget.UpdateBudgetRequestDTO;
import com.personal.finance_tracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    BudgetService budgetService;

    @GetMapping("")
    public ResponseEntity<List<BudgetResponseDTO>> getBudgets(@RequestParam("month") int month, @RequestParam("year") int year) {
        return ResponseEntity.ok(budgetService.getBudgets(month, year));
    }

    @PostMapping("")
    public ResponseEntity<BudgetResponseDTO> createBudget(@RequestBody @Valid CreateBudgetRequestDTO createBudgetRequestDTO) {
        return ResponseEntity.ok(budgetService.createBudget(createBudgetRequestDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(@PathVariable UUID id, @RequestBody UpdateBudgetRequestDTO updateBudgetRequestDTO) {
        return ResponseEntity.ok(budgetService.updateBudget(id, updateBudgetRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable UUID id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
