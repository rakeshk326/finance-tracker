package com.personal.finance_tracker.repository;

import com.personal.finance_tracker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUserIdAndDeletedAtIsNull(UUID userId);

    Optional<Account> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);
}
