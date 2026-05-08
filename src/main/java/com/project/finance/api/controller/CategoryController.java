package com.project.finance.api.controller;

import com.project.finance.api.documentation.ActiveUserRequired;
import com.project.finance.api.dto.request.CategoryRequestDto;
import com.project.finance.api.dto.response.CategoryResponseDto;
import com.project.finance.api.dto.response.ErrorResponse;
import com.project.finance.api.dto.response.ValidationErrorResponse;
import com.project.finance.api.mappers.CategoryMapper;
import com.project.finance.api.model.Category;
import com.project.finance.api.security.CustomUserDetails;
import com.project.finance.api.service.command.CategoryService;
import com.project.finance.api.service.query.CategoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Categories")
@ActiveUserRequired
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController implements GenericController {

    private final CategoryService categoryService;
    private final CategoryQueryService categoryQueryService;
    private final CategoryMapper categoryMapper;

    @Operation(summary = "Create category", description = """
            Creates a new category for the authenticated user and returns the Location header with the URI of the created resource.
            If an inactive category with the same name already exists, it will be reactivated instead of creating a duplicate category.
        """, responses = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(
                    schema = @Schema(implementation = ValidationErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Category name already in use", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping
    public ResponseEntity<Void> saveCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @Valid @RequestBody CategoryRequestDto dto) {
        Category category = categoryService.save(customUserDetails.getId(), dto);
        URI headerLocation = generateUriLocation(category.getId());
        return ResponseEntity.created(headerLocation).build();
    }

    @Operation(summary = "Get categories", responses = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategories(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<CategoryResponseDto> categories = categoryQueryService.findCategoriesByUserId(customUserDetails.getId()).stream()
                .map(categoryMapper::toResponse)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Update category", responses = {
            @ApiResponse(responseCode = "204", description = "Updated category"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(
                    schema = @Schema(implementation = ValidationErrorResponse.class)
            )),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Category name already in use", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                               @PathVariable Long id,
                                               @Valid @RequestBody CategoryRequestDto dto) {
        categoryService.update(customUserDetails.getId(), id, dto.name());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deactivate category", responses = {
            @ApiResponse(responseCode = "204", description = "Category deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Category is already inactive", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable Long id) {
        categoryService.deactivate(customUserDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }

}
