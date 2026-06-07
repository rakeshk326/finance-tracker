package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.account.AccountResponseDTO;
import com.personal.finance_tracker.dto.account.CreateAccountRequestDTO;
import com.personal.finance_tracker.dto.account.UpdateAccountRequestDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponseDTO createAccount(@Valid CreateAccountRequestDTO createAccountRequestDTO);

    List<AccountResponseDTO> getAccounts();

    AccountResponseDTO updateAccount(UUID id, UpdateAccountRequestDTO updateAccountRequestDTO);

    void deleteAccount(UUID id);
}
