package com.project.finance.api.service.command;

import com.project.finance.api.dto.request.CategoryRequestDto;
import com.project.finance.api.exceptions.ConflictException;
import com.project.finance.api.model.Category;
import com.project.finance.api.model.User;
import com.project.finance.api.repository.CategoryRepository;
import com.project.finance.api.service.query.CategoryQueryService;
import com.project.finance.api.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryService categoryQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public Category save(UUID userId, CategoryRequestDto dto) {
        User user = userQueryService.findById(userId);
        String name = dto.name().trim();

        return categoryRepository.findByUserIdAndNameIgnoreCase(user.getId(), name)
                .map(category -> {
                    if (!category.getActive()) {
                        category.setActive(true);
                        return categoryRepository.save(category);
                    }
                    throw new ConflictException("Category name already in use");
                })
                .orElseGet(() -> {
                    Category category = new Category(name, user);
                    user.addCategory(category);
                    return categoryRepository.save(category);
                });
    }

    @Transactional
    public void update(UUID userId, Long categoryId, String name) {
        Category category = categoryQueryService.findByIdAndUserId(categoryId, userId);
        String normalizedName = name.trim();

        validateUniqueCategoryNameForUpdate(userId, categoryId, normalizedName);
        category.setName(normalizedName);
    }

    @Transactional
    public void deactivate(UUID userId, Long categoryId) {
        Category category = categoryQueryService.findByIdAndUserId(categoryId, userId);

        if (!category.getActive()) {
            throw new ConflictException("Category is already inactive");
        }

        category.setActive(false);
    }

    private void validateUniqueCategoryNameForUpdate(UUID userId, Long currentCategoryId, String name) {
        categoryRepository.findByUserIdAndNameIgnoreCase(userId, name)
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(currentCategoryId)) {
                        throw new ConflictException("Category name already in use");
                    }
                });
    }

}
