package com.personal.finance_tracker.repository;

import com.personal.finance_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByDeletedAtIsNullAndUserIdOrDeletedAtIsNullAndUserIdIsNull(UUID userId);
    Optional<Category> findByIdAndDeletedAtIsNull(UUID categoryId);

    Optional<Category> findByIdAndUserIdAndDeletedAtIsNull(UUID categoryId, UUID userId);
}
