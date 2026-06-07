package com.personal.finance_tracker.mapper;

import com.personal.finance_tracker.dto.budget.CreateBudgetRequestDTO;
import com.personal.finance_tracker.dto.budget.BudgetResponseDTO;
import com.personal.finance_tracker.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    Budget toEntity(CreateBudgetRequestDTO createBudgetRequestDTO);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    BudgetResponseDTO toBudgetResponseDTO(Budget budget);
}
