package com.personal.finance_tracker.dto.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateTransferRequestDTO {

    private UUID accountId;

    private UUID toAccountId;

    private BigDecimal amount;

    private LocalDate date;

    private String description;
}
