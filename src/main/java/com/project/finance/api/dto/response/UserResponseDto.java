package com.project.finance.api.dto.response;

import java.time.LocalDateTime;

public record UserResponseDto(
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
