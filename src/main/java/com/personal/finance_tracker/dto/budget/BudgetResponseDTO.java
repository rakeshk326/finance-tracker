package com.personal.finance_tracker.dto.budget;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BudgetResponseDTO {
    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private int month;
    private int year;
}
