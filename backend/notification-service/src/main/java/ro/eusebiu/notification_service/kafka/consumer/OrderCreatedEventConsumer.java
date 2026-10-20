package ro.eusebiu.notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.eusebiu.notification_service.kafka.event.OrderCreatedEvent;

@Slf4j
@Component
public class OrderCreatedEventConsumer {

    @KafkaListener(
            topics = "order.created",
            groupId = "notification-service"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info(
                "OrderCreatedEvent received: orderId={}, eventId={}, userId={}, username={}, ticketsCount={}, totalPrice={}",
                event.orderId(),
                event.eventId(),
                event.userId(),
                event.username(),
                event.ticketsCount(),
                event.totalPrice()
        );
    }
}