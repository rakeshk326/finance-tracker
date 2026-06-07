package com.personal.finance_tracker.dto.loan;

import com.personal.finance_tracker.enums.LoanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateLoanRequestDTO {

    @NotBlank
    private String counterpartyName;

    @NotNull
    private LoanType direction;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private UUID accountId;

    @NotNull
    private LocalDate dueDate;

    private String description;
}