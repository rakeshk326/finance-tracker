package com.personal.finance_tracker.event.model;

import com.personal.finance_tracker.enums.LoanType;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanUpdatedEvent(
        UUID userId,
        UUID loanId,
        UUID oldAccountId,
        UUID newAccountId,
        LoanType oldLoanType,
        LoanType newLoanType,
        BigDecimal oldLoanAmount,
        BigDecimal newLoanAmount
) {
}
