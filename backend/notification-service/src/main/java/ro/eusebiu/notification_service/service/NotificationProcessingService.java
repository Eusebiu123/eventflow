package ro.eusebiu.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.eusebiu.notification_service.entity.ProcessedEvent;
import ro.eusebiu.notification_service.kafka.event.OrderCreatedEvent;
import ro.eusebiu.notification_service.repository.ProcessedEventRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessingService {

    private final ProcessedEventRepository processedEventRepository;
    private final EmailNotificationService emailNotificationService;

    public void processOrderCreated(OrderCreatedEvent event) {

        String messageId = event.messageId();

        if (messageId == null || messageId.isBlank()) {
            throw new IllegalArgumentException(
                    "Kafka messageId is missing for orderId: " + event.orderId()
            );
        }

        if (processedEventRepository.existsByEventId(messageId)) {
            log.info("Duplicate Kafka message ignored: {}", messageId);
            return;
        }

        if (event.email() == null || event.email().isBlank()) {
            throw new IllegalArgumentException(
                    "Email is missing for orderId: " + event.orderId()
            );
        }

        emailNotificationService.sendOrderCreatedEmail(
                event.email(),
                event.orderId(),
                event.eventTitle(),
                event.ticketsCount()
        );

        ProcessedEvent processedEvent = ProcessedEvent.builder()
                .eventId(messageId)
                .orderId(event.orderId())
                .processedAt(LocalDateTime.now())
                .build();

        processedEventRepository.saveAndFlush(processedEvent);

        log.info(
                "Notification processed successfully: messageId={}, orderId={}",
                messageId,
                event.orderId()
        );
    }
}