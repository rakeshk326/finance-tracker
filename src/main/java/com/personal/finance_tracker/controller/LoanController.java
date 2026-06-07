package com.personal.finance_tracker.controller;

import com.personal.finance_tracker.dto.loan.CreateLoanRequestDTO;
import com.personal.finance_tracker.dto.loan.LoanResponseDTO;
import com.personal.finance_tracker.dto.loan.UpdateLoanRequestDTO;
import com.personal.finance_tracker.enums.LoanStatusType;
import com.personal.finance_tracker.enums.LoanType;
import com.personal.finance_tracker.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    LoanService loanService;

    @GetMapping("")
    public Page<LoanResponseDTO> getLoans(
            @RequestParam(required = false) LoanType direction,
            @RequestParam(required = false) LoanStatusType statusType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return loanService.getLoans(direction, statusType, page, size);
    }

    @PostMapping("")
    public ResponseEntity<LoanResponseDTO> createLoan(@RequestBody @Valid CreateLoanRequestDTO createLoanRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(createLoanRequestDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> updateLoan(@PathVariable UUID id, @RequestBody UpdateLoanRequestDTO updateLoanRequestDTO) {
        return ResponseEntity.ok(loanService.updateLoan(id, updateLoanRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable UUID id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }

}
