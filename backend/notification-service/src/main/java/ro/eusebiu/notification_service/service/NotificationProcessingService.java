package ro.eusebiu.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.eusebiu.notification_service.entity.ProcessingStatus;
import ro.eusebiu.notification_service.kafka.event.OrderCreatedEvent;
import ro.eusebiu.notification_service.kafka.exception.EventAlreadyProcessingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessingService {

    private final EventClaimService eventClaimService;
    private final EmailNotificationService emailNotificationService;

    public void processOrderCreated(OrderCreatedEvent event) {

        String messageId = event.messageId();

        if (messageId == null || messageId.isBlank()) {
            throw new IllegalArgumentException("Missing messageId");
        }

        if (event.orderId() == null) {
            throw new IllegalArgumentException("Missing orderId");
        }

        if (event.email() == null || event.email().isBlank()) {
            throw new IllegalArgumentException("Missing email");
        }

        String claimToken = eventClaimService.claimEvent(
                messageId,
                event.orderId()
        );

        if (claimToken == null) {

            ProcessingStatus status =
                    eventClaimService.getStatus(messageId);

            if (status == ProcessingStatus.COMPLETED) {
                log.info(
                        "Duplicate Kafka message ignored: {}",
                        messageId
                );
                return;
            }

            throw new EventAlreadyProcessingException(messageId);
        }

        try {
            emailNotificationService.sendOrderCreatedEmail(
                    event.email(),
                    event.orderId(),
                    event.eventTitle(),
                    event.ticketsCount()
            );

            eventClaimService.markCompleted(
                    messageId,
                    claimToken
            );

            log.info(
                    "Notification processed successfully: messageId={}, orderId={}",
                    messageId,
                    event.orderId()
            );

        } catch (Exception ex) {

            eventClaimService.markFailed(
                    messageId,
                    claimToken
            );

            log.error(
                    "Notification processing failed: messageId={}",
                    messageId,
                    ex
            );

            throw ex;
        }
    }
}
