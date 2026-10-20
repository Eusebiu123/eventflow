package ro.eusebiu.event_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        @Size(max = 500)
        String description,

        @NotBlank
        @Size(max = 100)
        String city,

        @NotBlank
        @Size(max = 200)
        String venue,

        @NotNull
        LocalDateTime eventDate,

        @NotNull
        @Positive
        BigDecimal price,

        @NotNull
        @Min(1)
        Integer availableSeats
) {}