package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.loan.CreateLoanRequestDTO;
import com.personal.finance_tracker.dto.loan.LoanResponseDTO;
import com.personal.finance_tracker.dto.loan.UpdateLoanRequestDTO;
import com.personal.finance_tracker.entity.Account;
import com.personal.finance_tracker.entity.Loan;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.enums.LoanStatusType;
import com.personal.finance_tracker.enums.LoanType;
import com.personal.finance_tracker.event.model.LoanCreatedEvent;
import com.personal.finance_tracker.event.model.LoanDeletedEvent;
import com.personal.finance_tracker.event.model.LoanUpdatedEvent;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.mapper.LoanMapper;
import com.personal.finance_tracker.repository.AccountRepository;
import com.personal.finance_tracker.repository.LoanRepository;
import com.personal.finance_tracker.specification.LoanSpecification;
import com.personal.finance_tracker.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    LoanMapper loanMapper;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Page<LoanResponseDTO> getLoans(LoanType direction, LoanStatusType statusType, int page, int size) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Specification<Loan> spec = Specification.where(LoanSpecification.hasUserId(userId)).and(LoanSpecification.isNotDeleted());

        if(direction != null) {
            spec = spec.and(LoanSpecification.hasDirection(direction));
        }

        if(statusType != null) {
            spec = spec.and(LoanSpecification.hasLoanStatus(statusType));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));

        Page<Loan> loansPage = loanRepository.findAll(spec, pageable);
        return loansPage.map(loan -> loanMapper.toLoanResponseDTO(loan));
    }

    @Override
    @Transactional
    public LoanResponseDTO createLoan(CreateLoanRequestDTO createLoanRequestDTO) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Account account = accountRepository.findById(createLoanRequestDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Unauthorized access to account");
        }

        Loan loan = loanMapper.toEntity(createLoanRequestDTO);

        User user = new User();
        user.setId(userId);
        loan.setUser(user);
        loan.setStatus(LoanStatusType.PENDING);
        loan.setAccount(account);

        Loan createdLoan = loanRepository.save(loan);

        applicationEventPublisher.publishEvent(
                new LoanCreatedEvent(
                        createdLoan.getId(), createdLoan.getAccount().getId(), userId, createdLoan.getDirection(), createdLoan.getAmount(), createdLoan.getDescription()
                ));

        return loanMapper.toLoanResponseDTO(createdLoan);
    }

    @Override
    @Transactional
    public LoanResponseDTO updateLoan(UUID id, UpdateLoanRequestDTO updateLoanRequestDTO) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Loan loan = loanRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (!loan.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        UUID oldAccountId = loan.getAccount().getId();
        LoanType oldDirection = loan.getDirection();
        BigDecimal oldAmount = loan.getAmount();

        if(updateLoanRequestDTO.getAccountId() != null) {
            Account account = accountRepository.findById(updateLoanRequestDTO.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

            if (!account.getUser().getId().equals(userId)) {
                throw new AccessDeniedException("Unauthorized access to account");
            }
            loan.setAccount(account);
        }

        if(updateLoanRequestDTO.getCounterpartyName() != null) {
            loan.setCounterpartyName(updateLoanRequestDTO.getCounterpartyName());
        }

        if(updateLoanRequestDTO.getDirection() != null) {
            loan.setDirection(updateLoanRequestDTO.getDirection());
        }

        if (updateLoanRequestDTO.getAmount() != null) {
            loan.setAmount(updateLoanRequestDTO.getAmount());
        }

        if (updateLoanRequestDTO.getDueDate() != null) {
            loan.setDueDate(updateLoanRequestDTO.getDueDate());
        }

        if (updateLoanRequestDTO.getDescription() != null) {
            loan.setDescription(updateLoanRequestDTO.getDescription());
        }

        Loan updatedLoan = loanRepository.save(loan);

        if(updateLoanRequestDTO.getAccountId() != null || updateLoanRequestDTO.getDirection() != null || updateLoanRequestDTO.getAmount() != null) {
            applicationEventPublisher.publishEvent(
                    new LoanUpdatedEvent(userId, loan.getId(), oldAccountId, loan.getAccount().getId(), oldDirection, updatedLoan.getDirection(), oldAmount, loan.getAmount()
                    ));
        }

        return loanMapper.toLoanResponseDTO(updatedLoan);

    }

    @Override
    @Transactional
    public void deleteLoan(UUID id) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Loan loan = loanRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (!loan.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        loan.setDeletedAt(LocalDateTime.now());
        loanRepository.save(loan);

        applicationEventPublisher.publishEvent(
                new LoanDeletedEvent(
                        id, loan.getAccount().getId(), userId, loan.getDirection(), loan.getAmount(), loan.getDescription()
                ));
    }

}