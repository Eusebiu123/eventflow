package ro.eusebiu.identityservice.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String username,
        String role
) {}