package com.personal.finance_tracker.dto.category;

import com.personal.finance_tracker.enums.TransactionType;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoryResponseDTO {

    private UUID id;
    private String name;
    private TransactionType type;
}
