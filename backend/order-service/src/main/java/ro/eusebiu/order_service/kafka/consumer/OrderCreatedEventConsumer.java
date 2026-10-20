package ro.eusebiu.order_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.eusebiu.order_service.kafka.event.OrderCreatedEvent;

@Slf4j
@Component
public class OrderCreatedEventConsumer {

    @KafkaListener(
            topics = "order.created",
            groupId = "order-service"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info(
                "OrderCreatedEvent received: orderId={}, eventId={}, userId={}, ticketsCount={}, totalPrice={}",
                event.orderId(),
                event.eventId(),
                event.userId(),
                event.ticketsCount(),
                event.totalPrice()
        );
    }
}