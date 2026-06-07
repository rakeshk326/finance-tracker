package com.personal.finance_tracker.dto.budget;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateBudgetRequestDTO {

    private UUID categoryId;

    private BigDecimal limitAmount;

    private Integer month;

    private Integer year;
}
