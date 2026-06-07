package com.personal.finance_tracker.controller;

import com.personal.finance_tracker.dto.transaction.*;
import com.personal.finance_tracker.enums.TransactionType;
import com.personal.finance_tracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {

    @Autowired
    TransactionService transactionService;

    @GetMapping("")
    public Page<TransactionResponseDTO> getTransactions(
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getTransactions(accountId, type, startDate, endDate, categoryId, page, size);
    }

    @PostMapping("/income-expense")
    public ResponseEntity<TransactionResponseDTO> createIncomeExpenseTransaction(@RequestBody @Valid CreateIncomeExpenseRequestDTO createIncomeExpenseRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createIncomeExpenseTransaction(createIncomeExpenseRequestDTO));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> createTransferTransaction(@RequestBody @Valid CreateTransferRequestDTO createTransferRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransferTransaction(createTransferRequestDTO));
    }

    @PatchMapping("/income-expense/{id}")
    public ResponseEntity<TransactionResponseDTO> updateIncomeExpenseTransaction(@PathVariable UUID id, @RequestBody UpdateIncomeExpenseRequestDTO updateIncomeExpenseRequestDTO) {
        return ResponseEntity.ok(transactionService.updateIncomeExpenseTransaction(id, updateIncomeExpenseRequestDTO));
    }

    @PatchMapping("/transfer/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransferTransaction(@PathVariable UUID id, @RequestBody UpdateTransferRequestDTO updateTransferRequestDTO) {
        return ResponseEntity.ok(transactionService.updateTransferTransaction(id, updateTransferRequestDTO));
    }

    @DeleteMapping("/income-expense/{id}")
    public ResponseEntity<Void> deleteIncomeExpenseTransaction(@PathVariable UUID id) {
        transactionService.deleteIncomeExpenseTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/transfer/{id}")
    public ResponseEntity<Void> deleteTransferTransaction(@PathVariable UUID id) {
        transactionService.deleteTransferTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
