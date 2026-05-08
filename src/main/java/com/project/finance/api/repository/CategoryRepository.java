package com.project.finance.api.repository;

import com.project.finance.api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndUserId(Long id, UUID userId);

    Optional<Category> findByUserIdAndNameIgnoreCase(UUID userId, String name);

    List<Category> findByUserIdAndActiveTrue(UUID userId);

}
