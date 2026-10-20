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
import ro.eusebiu.order_service.repository.OrderRepository;
import ro.eusebiu.order_service.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final EventClient eventClient;

    @Override
    public OrderResponse create(
            CreateOrderRequest request,
            Authentication authentication
    ) {
        EventResponse event = eventClient.reserveSeats(
                request.eventId(),
                request.ticketsCount()
        );

        Long userId = (Long) authentication.getCredentials();
        String username = authentication.getName();

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