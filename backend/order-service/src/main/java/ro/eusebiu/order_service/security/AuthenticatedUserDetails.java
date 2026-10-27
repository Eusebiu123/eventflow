package ro.eusebiu.order_service.security;

public record AuthenticatedUserDetails(
        Long userId,
        String email
) {}