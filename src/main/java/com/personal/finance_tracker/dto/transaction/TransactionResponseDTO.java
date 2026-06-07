package com.personal.finance_tracker.dto.transaction;

import com.personal.finance_tracker.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionResponseDTO {

    private UUID id;
    private String accountName;
    private String toAccountName;
    private String categoryName;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

}