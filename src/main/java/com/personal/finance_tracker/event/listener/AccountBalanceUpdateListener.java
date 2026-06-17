package com.personal.finance_tracker.event.listener;

import com.personal.finance_tracker.entity.Account;
import com.personal.finance_tracker.event.model.LoanCreatedEvent;
import com.personal.finance_tracker.event.model.LoanDeletedEvent;
import com.personal.finance_tracker.event.model.LoanUpdatedEvent;
import com.personal.finance_tracker.exception.InsufficientBalanceException;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountBalanceUpdateListener {

    @Autowired
    AccountRepository accountRepository;

    @EventListener
    public void onLoanCreated(LoanCreatedEvent event) {

        Account account = accountRepository.findById(event.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(event.userId())) {
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

    @EventListener
    public void onLoanUpdated(LoanUpdatedEvent event) {

        Account oldAccount = accountRepository.findById(event.oldAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!oldAccount.getUser().getId().equals(event.userId())) {
            throw new AccessDeniedException("Unauthorized access to account");
        }

        switch (event.oldLoanType()) {
            case BORROWED -> {
                if (oldAccount.getCurrentBalance().compareTo(event.oldLoanAmount()) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance");
                }
                oldAccount.setCurrentBalance(oldAccount.getCurrentBalance().subtract(event.oldLoanAmount()));
            }
            case LENT ->
                    oldAccount.setCurrentBalance(oldAccount.getCurrentBalance().add(event.oldLoanAmount()));
        }

        accountRepository.save(oldAccount);

        Account newAccount = accountRepository.findById(event.newAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!newAccount.getUser().getId().equals(event.userId())) {
            throw new AccessDeniedException("Unauthorized access to account");
        }

        switch (event.newLoanType()) {
            case LENT -> {
                if (newAccount.getCurrentBalance().compareTo(event.newLoanAmount()) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance");
                }
                newAccount.setCurrentBalance(newAccount.getCurrentBalance().subtract(event.newLoanAmount()));
            }
            case BORROWED ->
                    newAccount.setCurrentBalance(newAccount.getCurrentBalance().add(event.newLoanAmount()));
        }

        accountRepository.save(newAccount);
    }

    @EventListener
    public void onLoanDeleted(LoanDeletedEvent event) {

        Account account = accountRepository.findById(event.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(event.userId())) {
            throw new AccessDeniedException("Unauthorized access to account");
        }

        BigDecimal amount = event.amount();

        switch (event.direction()) {
            case BORROWED -> {
                if (account.getCurrentBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance");
                }
                account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
            }
            case LENT ->
                    account.setCurrentBalance(account.getCurrentBalance().add(amount));
        }

        accountRepository.save(account);
    }

}