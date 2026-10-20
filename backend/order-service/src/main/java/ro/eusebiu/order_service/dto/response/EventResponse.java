package ro.eusebiu.order_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        String city,
        String venue,
        LocalDateTime eventDate,
        BigDecimal price,
        Integer availableSeats,
        boolean active
) {}