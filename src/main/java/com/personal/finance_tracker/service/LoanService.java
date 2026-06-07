package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.loan.CreateLoanRequestDTO;
import com.personal.finance_tracker.dto.loan.LoanResponseDTO;
import com.personal.finance_tracker.dto.loan.UpdateLoanRequestDTO;
import com.personal.finance_tracker.enums.LoanStatusType;
import com.personal.finance_tracker.enums.LoanType;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface LoanService {

    Page<LoanResponseDTO> getLoans(LoanType direction, LoanStatusType statusType, int page, int size);

    LoanResponseDTO createLoan(CreateLoanRequestDTO createLoanRequestDTO);

    LoanResponseDTO updateLoan(UUID id, UpdateLoanRequestDTO updateLoanRequestDTO);

    void deleteLoan(UUID id);
}
