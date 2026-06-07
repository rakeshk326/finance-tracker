package com.personal.finance_tracker.dto.account;

import com.personal.finance_tracker.enums.AccountType;
import lombok.Data;

@Data
public class UpdateAccountRequestDTO {

    private String name;

    private AccountType type;
}
