package com.personal.finance_tracker.event.listener;

import com.personal.finance_tracker.entity.Account;
import com.personal.finance_tracker.entity.Transaction;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.enums.TransactionType;
import com.personal.finance_tracker.event.model.LoanCreatedEvent;
import com.personal.finance_tracker.exception.InsufficientBalanceException;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.repository.AccountRepository;
import com.personal.finance_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TransactionUpdateListener {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @EventListener
    public void onLoanCreated(LoanCreatedEvent event) {

        Transaction transaction = new Transaction();

        Account account = accountRepository.findById(event.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(event.userId())) {
            throw new AccessDeniedException("Unauthorized access to account");
        }

        transaction.setAccount(account);

        User user = new User();
        user.setId(event.userId());
        transaction.setUser(user);

        switch (event.direction()) {
            case LENT -> transaction.setType(TransactionType.LOAN_LENT);
            case BORROWED -> transaction.setType(TransactionType.LOAN_BORROWED);
        }

        transaction.setAmount(event.amount());
        transaction.setDate(LocalDate.now());
        transaction.setDescription(event.description());

        transactionRepository.save(transaction);

    }
}