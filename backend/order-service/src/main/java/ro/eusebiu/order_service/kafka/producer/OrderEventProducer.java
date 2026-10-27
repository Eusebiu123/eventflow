package ro.eusebiu.order_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ro.eusebiu.order_service.kafka.event.OrderCreatedEvent;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String ORDER_CREATED_TOPIC = "order.created";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, OrderCreatedEvent>>
    sendOrderCreatedEvent(OrderCreatedEvent event) {

        return kafkaTemplate.send(
                "order.created",
                event.orderId().toString(),
                event
        );
    }
}