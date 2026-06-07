package com.personal.finance_tracker.dto.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateTransferRequestDTO {

    @NotNull
    private UUID accountId;

    @NotNull
    private UUID toAccountId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate date;

    private String description;
}
