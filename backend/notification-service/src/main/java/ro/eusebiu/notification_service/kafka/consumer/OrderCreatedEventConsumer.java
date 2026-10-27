package ro.eusebiu.notification_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.eusebiu.notification_service.kafka.event.OrderCreatedEvent;
import ro.eusebiu.notification_service.service.NotificationProcessingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {

    private final NotificationProcessingService notificationProcessingService;

    @KafkaListener(
            topics = "order.created",
            groupId = "notification-service"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        log.info(
                "Kafka event received: messageId={}, orderId={}",
                event.messageId(),
                event.orderId()
        );

        notificationProcessingService.processOrderCreated(event);

        log.info(
                "Kafka event processing completed: messageId={}",
                event.messageId()
        );
    }
}