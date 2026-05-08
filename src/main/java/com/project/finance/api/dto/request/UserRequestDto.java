package com.project.finance.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserRequestDto(

        @NotBlank(message = "Username is required")
        @Size(min = 5, max = 50, message = "Username length must be between 5 and 50 characters")
        String username,

        @NotEmpty(message = "Password is required")
        @Size(min = 8, max = 40, message = "Password length must be between 8 and 40 characters")
        String password

) {
}
