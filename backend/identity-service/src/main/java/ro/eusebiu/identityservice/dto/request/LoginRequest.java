package ro.eusebiu.identityservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String usernameOrEmail,

        @NotBlank
        String password
) {}