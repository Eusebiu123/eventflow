package ro.eusebiu.order_service.kafka.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.eusebiu.order_service.entity.OutboxEvent;
import ro.eusebiu.order_service.kafka.event.OrderCreatedEvent;
import ro.eusebiu.order_service.kafka.producer.OrderEventProducer;
import ro.eusebiu.order_service.repository.OutboxEventRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final OrderEventProducer orderEventProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        log.info("Outbox scheduler running...");

        List<OutboxEvent> events =
                outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc(
                        PageRequest.of(0, 100)
                );

        for (OutboxEvent event : events) {
            try {
                OrderCreatedEvent orderCreatedEvent =
                        objectMapper.readValue(
                                event.getPayload(),
                                OrderCreatedEvent.class
                        );

                orderEventProducer
                        .sendOrderCreatedEvent(orderCreatedEvent)
                        .get(10, TimeUnit.SECONDS);

                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());

                outboxEventRepository.save(event);

                log.info("Outbox event published: id={}", event.getId());

            } catch (Exception ex) {
                log.error(
                        "Failed to publish outbox event: id={}",
                        event.getId(),
                        ex
                );
            }
        }
    }
}