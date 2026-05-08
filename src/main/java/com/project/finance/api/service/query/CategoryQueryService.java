package com.project.finance.api.service.query;

import com.project.finance.api.exceptions.NotFoundException;
import com.project.finance.api.model.Category;
import com.project.finance.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Category findByIdAndUserId(Long id, UUID userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow( () -> new NotFoundException("Category not found") );
    }

    @Transactional(readOnly = true)
    public List<Category> findCategoriesByUserId(UUID userId) {
        return categoryRepository.findByUserIdAndActiveTrue(userId);
    }

}
