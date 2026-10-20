package ro.eusebiu.identityservice.dto.response;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role,
        boolean enabled
) {}