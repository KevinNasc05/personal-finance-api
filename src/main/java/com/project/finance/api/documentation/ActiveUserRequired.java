package com.project.finance.api.documentation;

import com.project.finance.api.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirement(name = "basicAuth")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
        )),
        @ApiResponse(responseCode = "403", description = "Your account is inactive. Reactivate your account to continue.", content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
        ))
})
public @interface ActiveUserRequired {
}
