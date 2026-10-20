package ro.eusebiu.order_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(

        @NotNull
        Long eventId,

        @NotNull
        @Positive
        Integer ticketsCount

) {
}