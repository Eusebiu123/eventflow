package ro.eusebiu.notification_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.eusebiu.notification_service.kafka.event.OrderCreatedEvent;
import ro.eusebiu.notification_service.service.EmailNotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {

    private final EmailNotificationService emailNotificationService;

    @KafkaListener(
            topics = "order.created",
            groupId = "notification-service"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        log.info(
                "OrderCreatedEvent received: orderId={}, eventId={}, userId={}, username={}, email={}, ticketsCount={}, totalPrice={}",
                event.orderId(),
                event.eventId(),
                event.userId(),
                event.username(),
                event.email(),
                event.ticketsCount(),
                event.totalPrice()
        );

        if (event.email() == null || event.email().isBlank()) {
            log.warn(
                    "Email notification skipped for orderId={}: missing email",
                    event.orderId()
            );
            return;
        }

        emailNotificationService.sendOrderCreatedEmail(
                event.email(),
                event.orderId(),
                event.eventTitle(),
                event.ticketsCount()
        );

        log.info(
                "Order notification sent successfully: orderId={}, email={}",
                event.orderId(),
                event.email()
        );
    }
}