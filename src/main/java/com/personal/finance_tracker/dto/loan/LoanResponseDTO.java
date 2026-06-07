package com.personal.finance_tracker.dto.loan;

import com.personal.finance_tracker.enums.LoanStatusType;
import com.personal.finance_tracker.enums.LoanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class LoanResponseDTO {
    private UUID id;
    private String counterpartyName;
    private LoanType direction;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LoanStatusType status;
    private String description;
}