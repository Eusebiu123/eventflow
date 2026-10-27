package ro.eusebiu.notification_service.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreatedEvent(
        String messageId,
        Long orderId,
        Long eventId,
        String eventTitle,
        Long userId,
        String username,
        String email,
        Integer ticketsCount,
        BigDecimal totalPrice,
        LocalDateTime createdAt
) {
}