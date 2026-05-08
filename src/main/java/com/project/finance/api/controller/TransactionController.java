package com.project.finance.api.controller;

import com.project.finance.api.documentation.ActiveUserRequired;
import com.project.finance.api.dto.request.TransactionRequestDto;
import com.project.finance.api.dto.response.ErrorResponse;
import com.project.finance.api.dto.response.PageResponseDto;
import com.project.finance.api.dto.response.TransactionResponseDto;
import com.project.finance.api.dto.response.ValidationErrorResponse;
import com.project.finance.api.mappers.TransactionMapper;
import com.project.finance.api.model.Transaction;
import com.project.finance.api.model.enums.TransactionType;
import com.project.finance.api.security.CustomUserDetails;
import com.project.finance.api.service.command.TransactionService;
import com.project.finance.api.service.query.TransactionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@Tag(name = "Transactions")
@ActiveUserRequired
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController implements GenericController {

    private final TransactionService transactionService;
    private final TransactionQueryService transactionQueryService;
    private final TransactionMapper transactionMapper;

    @Operation(summary = "Save transaction", description = "Creates a new financial transaction and updates the user's account balance.", responses = {
            @ApiResponse(responseCode = "201", description = "Created transaction"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(
                    schema = @Schema(implementation = ValidationErrorResponse.class)
            )),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Category is inactive", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping
    public ResponseEntity<Void> saveTransaction(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @Valid @RequestBody TransactionRequestDto dto) {
        Transaction transaction = transactionService.save(customUserDetails.getId(), dto);
        URI headerLocation = generateUriLocation(transaction.getId());
        return ResponseEntity.created(headerLocation).build();
    }

    @Operation(summary = "Get transaction details", responses = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> findTransactionById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                      @PathVariable Long id) {
        Transaction transactionFound = transactionQueryService.findById(customUserDetails.getId(), id);
        return ResponseEntity.ok(transactionMapper.toResponse(transactionFound));
    }

    @Operation(summary = "Search transactions", responses = {
            @ApiResponse(responseCode = "200", description = "Transactions found"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping
    public ResponseEntity<PageResponseDto<TransactionResponseDto>> searchTransactions(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC)
            Pageable pageable) {
        PageResponseDto<TransactionResponseDto> page = transactionQueryService
                .filter(customUserDetails.getId(), start, end, type, categoryId, pageable);

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Reverse transaction", description = """
        Creates a reversal transaction that offsets the original transaction and returns the
        Location header of the newly created reversal transaction.
        """, responses = {
            @ApiResponse(responseCode = "201", description = "Transaction reversed successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Transaction already reversed or insufficient balance", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/{id}/reverse")
    public ResponseEntity<Void> reverseTransaction(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable Long id) {
        Transaction reverseTransaction = transactionService.reverse(customUserDetails.getId(), id);
        URI headerLocation = generateUriLocation(reverseTransaction.getId());
        return ResponseEntity.created(headerLocation).build();
    }

}
