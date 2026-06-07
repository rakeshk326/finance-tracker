package com.personal.finance_tracker.mapper;

import com.personal.finance_tracker.dto.transaction.CreateIncomeExpenseRequestDTO;
import com.personal.finance_tracker.dto.transaction.TransactionResponseDTO;
import com.personal.finance_tracker.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "account", ignore = true)
    Transaction toEntity(CreateIncomeExpenseRequestDTO dto);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "account.name", target = "accountName")
    TransactionResponseDTO toTransactionResponseDTO(Transaction transaction);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "account.name", target = "accountName")
    List<TransactionResponseDTO> toTransactionResponseDTOList(List<Transaction> transactions);

}