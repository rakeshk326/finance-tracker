package com.personal.finance_tracker.dto.account;

import com.personal.finance_tracker.enums.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountResponseDTO {
    private UUID id;
    private String name;
    private AccountType type;
    private BigDecimal currentBalance;
}
