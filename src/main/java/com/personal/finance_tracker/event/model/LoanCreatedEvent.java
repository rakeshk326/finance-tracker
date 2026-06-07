package com.personal.finance_tracker.event.model;

import com.personal.finance_tracker.enums.LoanType;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanCreatedEvent(
        UUID loanId,
        UUID accountId,
        UUID userId,
        LoanType direction,
        BigDecimal amount,
        String description
) {
}