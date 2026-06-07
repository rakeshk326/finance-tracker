package com.personal.finance_tracker.dto.budget;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateBudgetRequestDTO {

    @NotNull
    private UUID categoryId;

    @NotNull
    private BigDecimal limitAmount;

    @Min(value = 1, message = "Month must be between 1 to 12")
    @Max(value = 12, message = "Month must be between 1 to 12")
    private int month;

    @NotNull
    private int year;
}
