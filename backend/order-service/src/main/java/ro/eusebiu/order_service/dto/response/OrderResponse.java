package ro.eusebiu.order_service.dto.response;

import ro.eusebiu.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long eventId,
        String eventTitle,
        Long userId,
        String username,
        Integer ticketsCount,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt
) {}