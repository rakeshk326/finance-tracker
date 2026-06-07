package com.personal.finance_tracker.dto.category;

import com.personal.finance_tracker.enums.TransactionType;
import lombok.Data;

@Data
public class UpdateCategoryRequestDTO {

    private String name;

    private TransactionType type;
}
