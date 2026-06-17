package com.personal.finance_tracker.event.listener;

import com.personal.finance_tracker.entity.Account;
import com.personal.finance_tracker.entity.Transaction;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.enums.TransactionType;
import com.personal.finance_tracker.event.model.LoanCreatedEvent;
import com.personal.finance_tracker.event.model.LoanDeletedEvent;
import com.personal.finance_tracker.event.model.LoanUpdatedEvent;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.repository.AccountRepository;
import com.personal.finance_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        transaction.setReferenceId(event.loanId());

        transactionRepository.save(transaction);

    }

    @EventListener
    public void onLoanUpdatedEvent(LoanUpdatedEvent event) {

        List<Transaction> transactionList = transactionRepository.findByReferenceIdAndDeletedAtIsNull(event.loanId());

        if(transactionList.isEmpty()) throw new ResourceNotFoundException("Transaction not found");

        Transaction transaction = transactionList.get(0);

        if (!transaction.getUser().getId().equals(event.userId())) {
            throw new AccessDeniedException("Access denied");
        }

        if(!transaction.getAccount().getId().equals(event.oldAccountId())) {
            Account account = accountRepository.findById(event.newAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

            if (!account.getUser().getId().equals(event.userId())) {
                throw new AccessDeniedException("Unauthorized access to account");
            }
            transaction.setAccount(account);
        }

        if(event.oldLoanType() != event.newLoanType()) {
            switch (event.newLoanType()) {
                case LENT -> transaction.setType(TransactionType.LOAN_LENT);
                case BORROWED -> transaction.setType(TransactionType.LOAN_BORROWED);
            }
        }

        if(!transaction.getAmount().equals(event.oldLoanAmount())) {
            transaction.setAmount(event.newLoanAmount());
        }
    }

    @EventListener
    public void onLoanDeleted(LoanDeletedEvent event) {

        List<Transaction> transactionList = transactionRepository.findByReferenceIdAndDeletedAtIsNull(event.loanId());

        if(transactionList.isEmpty()) throw new ResourceNotFoundException("Transaction not found");

        Transaction transaction = transactionList.get(0);

        if (!transaction.getUser().getId().equals(event.userId())) {
            throw new AccessDeniedException("Access denied");
        }

        transaction.setDeletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
}