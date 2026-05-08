package com.project.finance.api.controller;

import com.project.finance.api.documentation.ActiveUserRequired;
import com.project.finance.api.dto.response.BankAccountResponseDto;
import com.project.finance.api.dto.response.BankAccountSummaryDto;
import com.project.finance.api.dto.response.ErrorResponse;
import com.project.finance.api.mappers.BankAccountMapper;
import com.project.finance.api.model.BankAccount;
import com.project.finance.api.security.CustomUserDetails;
import com.project.finance.api.service.query.BankAccountQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Bank Accounts")
@ActiveUserRequired
@RestController
@RequestMapping("/api/v1/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountQueryService bankAccountQueryService;
    private final BankAccountMapper bankAccountMapper;

    @Operation(summary = "Get bank account details", responses = {
            @ApiResponse(responseCode = "200", description = "Bank account retrieved successfully")
    })
    @GetMapping("/me")
    public ResponseEntity<BankAccountResponseDto> getBankAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        BankAccount accountFound = bankAccountQueryService.findByUserId(customUserDetails.getId());
        return ResponseEntity.ok(bankAccountMapper.toResponse(accountFound));
    }

    @Operation(summary = "Get account summary", responses = {
            @ApiResponse(responseCode = "200", description = "Account summary retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Start date cannot be after end date", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping("/me/summary")
    public ResponseEntity<BankAccountSummaryDto> getSummary(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        BankAccountSummaryDto summary = bankAccountQueryService.getSummary(customUserDetails.getId(), startDate, endDate);
        return ResponseEntity.ok(summary);
    }

}
