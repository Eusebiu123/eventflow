package ro.eusebiu.order_service.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreatedEvent(
        Long orderId,
        Long eventId,
        String eventTitle,
        Long userId,
        String username,
        Integer ticketsCount,
        BigDecimal totalPrice,
        LocalDateTime createdAt
) {
}