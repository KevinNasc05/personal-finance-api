package com.project.finance.api.mappers;

import com.project.finance.api.dto.response.CategoryResponseDto;
import com.project.finance.api.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDto toResponse(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName()
        );
    }

}
