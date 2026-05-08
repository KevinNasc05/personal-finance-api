package com.project.finance.api.dto.request;

import com.project.finance.api.model.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusDto(

        @NotNull(message = "User status is required")
        UserStatus status

) {
}
