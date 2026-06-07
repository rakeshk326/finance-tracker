package com.personal.finance_tracker.event.listener;

import com.personal.finance_tracker.entity.Account;
import com.personal.finance_tracker.event.model.LoanCreatedEvent;
import com.personal.finance_tracker.exception.InsufficientBalanceException;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class AccountBalanceUpdateListener {

    @Autowired
    AccountRepository accountRepository;

    @EventListener
    public void onLoanCreated(LoanCreatedEvent event) {

        UUID userId = event.userId();

        Account account = accountRepository.findById(event.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Unauthorized access to account");
        }

        BigDecimal amount = event.amount();

        switch (event.direction()) {
            case LENT -> {
                if (account.getCurrentBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance");
                }
                account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
            }
            case BORROWED ->
                    account.setCurrentBalance(account.getCurrentBalance().add(amount));
        }

        accountRepository.save(account);
    }
}