package com.personal.finance_tracker.dto.transaction;

import com.personal.finance_tracker.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateIncomeExpenseRequestDTO {

    private UUID accountId;

    private UUID categoryId;

    private TransactionType type;

    private BigDecimal amount;

    private LocalDate date;

    private String description;
}
