package com.personal.finance_tracker.mapper;

import com.personal.finance_tracker.dto.account.AccountResponseDTO;
import com.personal.finance_tracker.dto.account.CreateAccountRequestDTO;
import com.personal.finance_tracker.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "user", ignore = true)
    Account toAccountEntity(CreateAccountRequestDTO createAccountRequestDTO);

    AccountResponseDTO toResponseDTO(Account account);

    List<AccountResponseDTO> toResponseListDTO(List<Account> accounts);
}
