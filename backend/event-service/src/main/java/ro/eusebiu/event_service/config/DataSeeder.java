package ro.eusebiu.event_service.config;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.eusebiu.event_service.entity.Event;
import ro.eusebiu.event_service.repository.EventRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedEvents();
    }

    private void seedEvents() {
        if (eventRepository.count() > 0) {
            return;
        }

        Event event1 = Event.builder()
                .title("Concert Rock")
                .description("Un concert live cu trupe rock locale")
                .city("Iasi")
                .venue("Sala Polivalenta")
                .eventDate(LocalDateTime.of(2026, 9, 10, 19, 0))
                .price(new BigDecimal("150.00"))
                .availableSeats(200)
                .active(true)
                .build();

        Event event2 = Event.builder()
                .title("Tech Conference")
                .description("Conferinta despre Java, Spring si microservicii")
                .city("Bucuresti")
                .venue("Romexpo")
                .eventDate(LocalDateTime.of(2026, 10, 5, 10, 0))
                .price(new BigDecimal("250.00"))
                .availableSeats(500)
                .active(true)
                .build();

        eventRepository.save(event1);
        eventRepository.save(event2);
    }
}
