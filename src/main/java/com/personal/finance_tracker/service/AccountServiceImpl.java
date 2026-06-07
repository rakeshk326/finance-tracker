package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.account.AccountResponseDTO;
import com.personal.finance_tracker.dto.account.CreateAccountRequestDTO;
import com.personal.finance_tracker.dto.account.UpdateAccountRequestDTO;
import com.personal.finance_tracker.entity.Account;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.mapper.AccountMapper;
import com.personal.finance_tracker.repository.AccountRepository;
import com.personal.finance_tracker.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountMapper accountMapper;

    @Override
    public List<AccountResponseDTO> getAccounts() {

        UUID userId = SecurityUtil.getCurrentUserId();

        List<Account> accounts = accountRepository.findByUserIdAndDeletedAtIsNull(userId);
        return accountMapper.toResponseListDTO(accounts);
    }

    @Override
    public AccountResponseDTO createAccount(CreateAccountRequestDTO createAccountRequestDTO) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Account account = accountMapper.toAccountEntity(createAccountRequestDTO);
        User user = new User();
        user.setId(userId);
        account.setUser(user);
        account.setCurrentBalance(BigDecimal.ZERO);

        Account accountSaved = accountRepository.save(account);
        return accountMapper.toResponseDTO(accountSaved);
    }

    @Override
    public AccountResponseDTO updateAccount(UUID id, UpdateAccountRequestDTO updateAccountRequestDTO) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Account account = accountRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account unavailable"));

        if (updateAccountRequestDTO.getName() != null) {
            account.setName(updateAccountRequestDTO.getName());
        }

        if (updateAccountRequestDTO.getType() != null) {
            account.setType(updateAccountRequestDTO.getType());
        }

        Account accountSaved = accountRepository.save(account);
        return accountMapper.toResponseDTO(accountSaved);
    }

    @Override
    public void deleteAccount(UUID id) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Account account = accountRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account unavailable"));

        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
    }


}
