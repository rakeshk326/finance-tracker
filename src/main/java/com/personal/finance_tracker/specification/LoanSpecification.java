package com.personal.finance_tracker.specification;

import com.personal.finance_tracker.entity.Loan;
import com.personal.finance_tracker.enums.LoanStatusType;
import com.personal.finance_tracker.enums.LoanType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class LoanSpecification {

    public static Specification<Loan> hasUserId(UUID userId) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Loan> hasDirection(LoanType direction) {
        return (root, query, cb) ->
                cb.equal(root.get("direction"), direction);
    }

    public static Specification<Loan> hasLoanStatus(LoanStatusType statusType) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), statusType);
    }

    public static Specification<Loan> betweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) ->
                cb.between(root.get("date"), startDate, endDate);
    }

    public static Specification<Loan> isNotDeleted() {
        return (root, query, cb) ->
                cb.isNull(root.get("deletedAt"));
    }


}
