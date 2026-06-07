package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.transaction.*;
import com.personal.finance_tracker.enums.TransactionType;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.util.UUID;

public interface TransactionService {

    Page<TransactionResponseDTO> getTransactions(UUID accountId, TransactionType type, LocalDate startDate, LocalDate endDate, UUID categoryId, int page, int size);

    TransactionResponseDTO createIncomeExpenseTransaction(CreateIncomeExpenseRequestDTO dto);

    TransactionResponseDTO updateIncomeExpenseTransaction(UUID transactionId, UpdateIncomeExpenseRequestDTO updateIncomeExpenseRequestDTO);

    void deleteIncomeExpenseTransaction(UUID id);

    TransactionResponseDTO createTransferTransaction(@Valid CreateTransferRequestDTO createTransferRequestDTO);

    TransactionResponseDTO updateTransferTransaction(UUID id, UpdateTransferRequestDTO updateTransferRequestDTO);

    void deleteTransferTransaction(UUID id);
}
