package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.transaction.*;
import com.personal.finance_tracker.entity.Account;
import com.personal.finance_tracker.entity.Category;
import com.personal.finance_tracker.entity.Transaction;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.enums.TransactionType;
import com.personal.finance_tracker.enums.TransferType;
import com.personal.finance_tracker.exception.InsufficientBalanceException;
import com.personal.finance_tracker.exception.InvalidTransactionTypeException;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.repository.AccountRepository;
import org.springframework.security.access.AccessDeniedException;
import com.personal.finance_tracker.mapper.TransactionMapper;
import com.personal.finance_tracker.repository.CategoryRepository;
import com.personal.finance_tracker.repository.TransactionRepository;
import com.personal.finance_tracker.specification.TransactionSpecification;
import com.personal.finance_tracker.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionMapper transactionMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public Page<TransactionResponseDTO> getTransactions(UUID accountId, TransactionType transactionType, LocalDate startDate, LocalDate endDate, UUID categoryId, int page, int size) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Specification<Transaction> spec = Specification.where(TransactionSpecification.hasUserId(userId)).and(TransactionSpecification.isNotDeleted());

        if (accountId != null) {
            spec = spec.and(TransactionSpecification.belongsToAccount(accountId));
        }

        if (transactionType != null) {
            spec = spec.and(TransactionSpecification.hasType(transactionType));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(TransactionSpecification.betweenDates(startDate, endDate));
        }

        if (categoryId != null) {
            spec = spec.and(TransactionSpecification.hasCategoryId(categoryId));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        Page<Transaction> transactionsPage = transactionRepository.findAll(spec, pageable);
        return transactionsPage.map(transaction -> transactionMapper.toTransactionResponseDTO(transaction));
    }

    @Override
    @Transactional
    public TransactionResponseDTO createIncomeExpenseTransaction(CreateIncomeExpenseRequestDTO req) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Account account = accountRepository.findById(req.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Unauthorized access to account");
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        BigDecimal amount = req.getAmount();

        switch (req.getType()) {
            case EXPENSE -> {
                if (account.getCurrentBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance");
                }
                account.setCurrentBalance(account.getCurrentBalance().subtract(req.getAmount()));
            }
            case INCOME ->
                    account.setCurrentBalance(account.getCurrentBalance().add(req.getAmount()));
        }

        accountRepository.save(account);

        Transaction transaction = transactionMapper.toEntity(req);
        User user = new User();
        user.setId(userId);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAccount(account);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toTransactionResponseDTO(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO updateIncomeExpenseTransaction(UUID transactionId, UpdateIncomeExpenseRequestDTO req) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Transaction transaction = transactionRepository.findByIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        Account oldAccount = transaction.getAccount();
        Account newAccount = oldAccount;

        BigDecimal oldAmount = transaction.getAmount();
        TransactionType oldType = transaction.getType();

        if (req.getAccountId() != null &&
                !req.getAccountId().equals(oldAccount.getId())) {

            newAccount = accountRepository.findById(req.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

            if (!newAccount.getUser().getId().equals(userId)) {
                throw new AccessDeniedException("Unauthorized account");
            }
        }

        switch (oldType) {
            case EXPENSE ->
                    oldAccount.setCurrentBalance(oldAccount.getCurrentBalance().add(oldAmount));
            case INCOME ->
                    oldAccount.setCurrentBalance(oldAccount.getCurrentBalance().subtract(oldAmount));
            default ->
                    throw new InvalidTransactionTypeException("Invalid transaction type for this API");
        }

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            transaction.setCategory(category);
        }

        if (req.getType() != null) {
            transaction.setType(req.getType());
        }

        if (req.getAmount() != null) {
            transaction.setAmount(req.getAmount());
        }

        if (req.getDate() != null) {
            transaction.setDate(req.getDate());
        }

        if (req.getDescription() != null) {
            transaction.setDescription(req.getDescription());
        }

        BigDecimal newAmount = transaction.getAmount();
        TransactionType newType = transaction.getType();

        switch (newType) {
            case EXPENSE -> {
                if (newAccount.getCurrentBalance().compareTo(newAmount) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance");
                }
                newAccount.setCurrentBalance(newAccount.getCurrentBalance().subtract(newAmount));
            }
            case INCOME ->
                    newAccount.setCurrentBalance(newAccount.getCurrentBalance().add(newAmount));
            default ->
                    throw new InvalidTransactionTypeException("Invalid transaction type");
        }

        accountRepository.save(oldAccount);

        if (!oldAccount.getId().equals(newAccount.getId())) {
            accountRepository.save(newAccount);
        }

        transaction.setAccount(newAccount);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionResponseDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteIncomeExpenseTransaction(UUID transactionId) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Transaction transaction = transactionRepository.findByIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        Account account = transaction.getAccount();
        BigDecimal amount = transaction.getAmount();

        switch (transaction.getType()) {
            case EXPENSE ->
                    account.setCurrentBalance(account.getCurrentBalance().add(amount));
            case INCOME ->
                    account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        }

        accountRepository.save(account);

        transaction.setDeletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO createTransferTransaction(CreateTransferRequestDTO req) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Account account = accountRepository.findById(req.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Account toAccount = accountRepository.findById(req.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        if (!account.getUser().getId().equals(userId) || !toAccount.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Unauthorized access to account");
        }

        BigDecimal amount = req.getAmount();

        if (account.getCurrentBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        account.setCurrentBalance(account.getCurrentBalance().subtract(req.getAmount()));
        toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(req.getAmount()));

        accountRepository.saveAll(List.of(account, toAccount));

        UUID referenceId = UUID.randomUUID();

        Transaction sourceTransaction = new Transaction();
        sourceTransaction.setUser(account.getUser());
        sourceTransaction.setAccount(account);
        sourceTransaction.setType(TransactionType.TRANSFER);
        sourceTransaction.setTransferType(TransferType.DEBIT);
        sourceTransaction.setAmount(amount);
        sourceTransaction.setDate(req.getDate());
        sourceTransaction.setDescription(req.getDescription());
        sourceTransaction.setReferenceId(referenceId);

        Transaction destinationTransaction = new Transaction();
        destinationTransaction.setUser(toAccount.getUser());
        destinationTransaction.setAccount(toAccount);
        destinationTransaction.setType(TransactionType.TRANSFER);
        destinationTransaction.setTransferType(TransferType.CREDIT);
        destinationTransaction.setAmount(amount);
        destinationTransaction.setDate(req.getDate());
        destinationTransaction.setDescription(req.getDescription());
        destinationTransaction.setReferenceId(referenceId);

        transactionRepository.saveAll(List.of(sourceTransaction, destinationTransaction));

        TransactionResponseDTO savedTransaction = transactionMapper.toTransactionResponseDTO(sourceTransaction);
        savedTransaction.setToAccountName(toAccount.getName());
        return savedTransaction;
    }

    @Override
    @Transactional
    public TransactionResponseDTO updateTransferTransaction(UUID id, UpdateTransferRequestDTO req) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Transaction transaction = transactionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        List<Transaction> transferGroup = transactionRepository.findByReferenceIdAndDeletedAtIsNull(transaction.getReferenceId());

        Transaction oldSourceTransaction = transferGroup.stream().filter(tx -> tx.getTransferType() == TransferType.DEBIT)
                .findFirst().orElseThrow();

        Transaction oldDestinationTransaction = transferGroup.stream().filter(tx -> tx.getTransferType() == TransferType.CREDIT)
                .findFirst().orElseThrow();

        boolean needsBalanceCalculation = req.getAccountId() != null || req.getToAccountId() != null || req.getAmount() != null;

        if (needsBalanceCalculation) {
            Account oldSourceAccount = oldSourceTransaction.getAccount();
            Account oldDestinationAccount = oldDestinationTransaction.getAccount();

            oldSourceAccount.setCurrentBalance(oldSourceAccount.getCurrentBalance().add(oldSourceTransaction.getAmount()));
            oldDestinationAccount.setCurrentBalance(oldDestinationAccount.getCurrentBalance().subtract(oldSourceTransaction.getAmount()));
            accountRepository.saveAll(List.of(oldSourceAccount, oldDestinationAccount));

            Account newSourceAccount = req.getAccountId() != null ? accountRepository.findByIdAndUserIdAndDeletedAtIsNull(req.getAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Source account not found")) : oldSourceAccount;
            Account newDestinationAccount = req.getToAccountId() != null ? accountRepository.findByIdAndUserIdAndDeletedAtIsNull(req.getToAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Destination account not found")) : oldDestinationAccount;

            BigDecimal newAmount = req.getAmount() != null ? req.getAmount() : oldSourceTransaction.getAmount();

            if (newSourceAccount.getCurrentBalance().compareTo(newAmount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance to update transfer");
            }

            newSourceAccount.setCurrentBalance(newSourceAccount.getCurrentBalance().subtract(newAmount));
            newDestinationAccount.setCurrentBalance(newDestinationAccount.getCurrentBalance().add(newAmount));
            accountRepository.saveAll(List.of(newSourceAccount, newDestinationAccount));

            oldSourceTransaction.setAccount(newSourceAccount);
            oldSourceTransaction.setAmount(newAmount);
            oldDestinationTransaction.setAccount(newDestinationAccount);
            oldDestinationTransaction.setAmount(newAmount);
        }

        if (req.getDate() != null) {
            oldSourceTransaction.setDate(req.getDate());
            oldDestinationTransaction.setDate(req.getDate());
        }

        if (req.getDescription() != null) {
            oldSourceTransaction.setDescription(req.getDescription());
            oldDestinationTransaction.setDescription(req.getDescription());
        }

        transactionRepository.saveAll(List.of(oldSourceTransaction, oldDestinationTransaction));

        TransactionResponseDTO responseDTO = transactionMapper.toTransactionResponseDTO(oldSourceTransaction);
        responseDTO.setToAccountName(oldDestinationTransaction.getAccount().getName());
        return responseDTO;
    }

    @Override
    @Transactional
    public void deleteTransferTransaction(UUID transactionId) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Transaction transaction = transactionRepository.findByIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        List<Transaction> transferGroup = transactionRepository.findByReferenceIdAndDeletedAtIsNull(transaction.getReferenceId());

        Transaction oldSourceTransaction = transferGroup.stream().filter(tx -> tx.getTransferType() == TransferType.DEBIT)
                .findFirst().orElseThrow();

        Transaction oldDestinationTransaction = transferGroup.stream().filter(tx -> tx.getTransferType() == TransferType.CREDIT)
                .findFirst().orElseThrow();

        Account oldSourceAccount = oldSourceTransaction.getAccount();
        Account oldDestinationAccount = oldDestinationTransaction.getAccount();

        oldSourceAccount.setCurrentBalance(oldSourceAccount.getCurrentBalance().add(oldSourceTransaction.getAmount()));
        oldDestinationAccount.setCurrentBalance(oldDestinationAccount.getCurrentBalance().subtract(oldSourceTransaction.getAmount()));
        accountRepository.saveAll(List.of(oldSourceAccount, oldDestinationAccount));

        oldSourceTransaction.setDeletedAt(LocalDateTime.now());
        oldDestinationTransaction.setDeletedAt(LocalDateTime.now());
        transactionRepository.saveAll(List.of(oldSourceTransaction, oldDestinationTransaction));
    }

}