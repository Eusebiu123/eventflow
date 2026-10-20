package ro.eusebiu.order_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ro.eusebiu.order_service.dto.response.EventResponse;

@Component
@RequiredArgsConstructor
public class EventClient {

    private final RestClient restClient;

    @Value("${event.service.url}")
    private String eventServiceUrl;

    public EventResponse getEventById(Long eventId) {
        return restClient.get()
                .uri(eventServiceUrl + "/api/events/{id}", eventId)
                .retrieve()
                .body(EventResponse.class);
    }

    public EventResponse reserveSeats(Long eventId, Integer ticketsCount) {
        return restClient.post()
                .uri(eventServiceUrl + "/api/events/{id}/reserve?ticketsCount={ticketsCount}", eventId, ticketsCount)
                .retrieve()
                .body(EventResponse.class);
    }
}