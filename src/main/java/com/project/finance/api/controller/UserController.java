package com.project.finance.api.controller;

import com.project.finance.api.documentation.ActiveUserRequired;
import com.project.finance.api.dto.request.UpdateUserStatusDto;
import com.project.finance.api.dto.request.UserRequestDto;
import com.project.finance.api.dto.response.ErrorResponse;
import com.project.finance.api.dto.response.UserResponseDto;
import com.project.finance.api.dto.response.ValidationErrorResponse;
import com.project.finance.api.mappers.UserMapper;
import com.project.finance.api.model.User;
import com.project.finance.api.security.CustomUserDetails;
import com.project.finance.api.service.command.UserService;
import com.project.finance.api.service.query.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements GenericController {

    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserMapper userMapper;

    @Operation(summary = "Create user", description = "Creates a new user and returns the Location header pointing to /api/v1/users/me", responses = {
            @ApiResponse(responseCode = "201", description = "Created user"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(
                    schema = @Schema(implementation = ValidationErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping
    public ResponseEntity<Void> saveUser(@Valid @RequestBody UserRequestDto dto) {
        userService.save(userMapper.toEntity(dto));
        URI headerLocation = generateUserUriLocation();
        return ResponseEntity.created(headerLocation).build();
    }

    @SecurityRequirement(name = "basicAuth")
    @Operation(summary = "Get current user", responses = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User userFound = userQueryService.findById(customUserDetails.getId());
        return ResponseEntity.ok(userMapper.toResponse(userFound));
    }

    @ActiveUserRequired
    @Operation(summary = "Update user", responses = {
            @ApiResponse(responseCode = "204", description = "Updated user"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(
                    schema = @Schema(implementation = ValidationErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/me")
    public ResponseEntity<Void> updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @Valid @RequestBody UserRequestDto dto) {
        userService.update(customUserDetails.getId(), userMapper.toEntity(dto));
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "basicAuth")
    @Operation(summary = "Update user status", description = "Allows inactive users to reactivate their account.", responses = {
            @ApiResponse(responseCode = "204", description = "Updated user status"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(
                    schema = @Schema(implementation = ValidationErrorResponse.class)
            )),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PatchMapping("/me/status")
    public ResponseEntity<Void> updateUserStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                 @Valid @RequestBody UpdateUserStatusDto dto) {
        userService.updateStatus(customUserDetails.getId(), dto.status());
        return ResponseEntity.noContent().build();
    }

}
