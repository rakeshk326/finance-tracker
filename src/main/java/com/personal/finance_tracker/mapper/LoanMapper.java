package com.personal.finance_tracker.mapper;

import com.personal.finance_tracker.dto.loan.CreateLoanRequestDTO;
import com.personal.finance_tracker.dto.loan.LoanResponseDTO;
import com.personal.finance_tracker.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "user", ignore = true)
    Loan toEntity(CreateLoanRequestDTO createLoanRequestDTO);

    LoanResponseDTO toLoanResponseDTO(Loan loan);
}
