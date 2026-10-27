package ro.eusebiu.order_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.eusebiu.order_service.client.EventClient;
import ro.eusebiu.order_service.dto.request.CreateOrderRequest;
import ro.eusebiu.order_service.dto.response.EventResponse;
import ro.eusebiu.order_service.dto.response.OrderResponse;
import ro.eusebiu.order_service.entity.Order;
import ro.eusebiu.order_service.entity.OrderStatus;
import ro.eusebiu.order_service.exception.ResourceNotFoundException;
import ro.eusebiu.order_service.kafka.event.OrderCreatedEvent;
import ro.eusebiu.order_service.repository.OrderRepository;
import ro.eusebiu.order_service.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import ro.eusebiu.order_service.kafka.producer.OrderEventProducer;
import ro.eusebiu.order_service.security.AuthenticatedUserDetails;
import tools.jackson.databind.ObjectMapper;
import ro.eusebiu.order_service.entity.OutboxEvent;
import ro.eusebiu.order_service.repository.OutboxEventRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final EventClient eventClient;
    private final OrderEventProducer orderEventProducer;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public OrderResponse create(
            CreateOrderRequest request,
            Authentication authentication
    ) {
        AuthenticatedUserDetails details =
                (AuthenticatedUserDetails) authentication.getDetails();

        Long userId = details.userId();
        String email = details.email();
        String username = authentication.getName();

        EventResponse event = eventClient.reserveSeats(
                request.eventId(),
                request.ticketsCount()
        );

        BigDecimal totalPrice = event.price()
                .multiply(BigDecimal.valueOf(request.ticketsCount()));

        Order order = Order.builder()
                .eventId(event.id())
                .eventTitle(event.title())
                .userId(userId)
                .username(username)
                .ticketsCount(request.ticketsCount())
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        String messageId = UUID.randomUUID().toString();

        OrderCreatedEvent eventCreated = new OrderCreatedEvent(
                messageId,
                savedOrder.getId(),
                savedOrder.getEventId(),
                savedOrder.getEventTitle(),
                savedOrder.getUserId(),
                savedOrder.getUsername(),
                email,
                savedOrder.getTicketsCount(),
                savedOrder.getTotalPrice(),
                savedOrder.getCreatedAt()
        );

        String payload = objectMapper.writeValueAsString(eventCreated);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType("ORDER")
                .aggregateId(savedOrder.getId())
                .eventType("OrderCreatedEvent")
                .payload(payload)
                .published(false)
                .createdAt(LocalDateTime.now())
                .build();

        outboxEventRepository.save(outboxEvent);

        return toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getAllForUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getEventId(),
                order.getEventTitle(),
                order.getUserId(),
                order.getUsername(),
                order.getTicketsCount(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}