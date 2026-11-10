
package ro.eusebiu.order_service.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ro.eusebiu.order_service.client.EventClient;
import ro.eusebiu.order_service.dto.request.CreateOrderRequest;
import ro.eusebiu.order_service.dto.response.EventResponse;
import ro.eusebiu.order_service.dto.response.OrderResponse;
import ro.eusebiu.order_service.entity.Order;
import ro.eusebiu.order_service.entity.OrderStatus;
import ro.eusebiu.order_service.entity.OutboxEvent;
import ro.eusebiu.order_service.kafka.event.OrderCreatedEvent;
import ro.eusebiu.order_service.kafka.producer.OrderEventProducer;
import ro.eusebiu.order_service.repository.OrderRepository;
import ro.eusebiu.order_service.repository.OutboxEventRepository;
import ro.eusebiu.order_service.security.AuthenticatedUserDetails;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventClient eventClient;

    @Mock
    private OrderEventProducer orderEventProducer;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderAndSaveOutboxEvent() {

        CreateOrderRequest request = new CreateOrderRequest(1L, 2);

        EventResponse event = mock(EventResponse.class);

        when(event.id()).thenReturn(1L);
        when(event.title()).thenReturn("Concert Rock");
        when(event.price()).thenReturn(new BigDecimal("150.00"));

        when(authentication.getDetails()).thenReturn(
                new AuthenticatedUserDetails(2L, "testuser@gmail.com")
        );

        when(authentication.getName()).thenReturn("testuser");

        when(eventClient.reserveSeats(1L, 2)).thenReturn(event);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(15L);
            return order;
        });

        when(objectMapper.writeValueAsString(any(OrderCreatedEvent.class)))
                .thenReturn("{\"messageId\":\"test-message\"}");

        OrderResponse response = orderService.create(request, authentication);

        assertNotNull(response);
        assertEquals(15L, response.id());
        assertEquals(1L, response.eventId());
        assertEquals("Concert Rock", response.eventTitle());
        assertEquals(new BigDecimal("300.00"), response.totalPrice());
        assertEquals(OrderStatus.PENDING, response.status());

        verify(eventClient).reserveSeats(1L, 2);
        verify(orderRepository).save(any(Order.class));
        verify(outboxEventRepository).save(any(OutboxEvent.class));

        verifyNoInteractions(orderEventProducer);

        ArgumentCaptor<OutboxEvent> captor =
                ArgumentCaptor.forClass(OutboxEvent.class);

        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent savedEvent = captor.getValue();

        assertEquals("ORDER", savedEvent.getAggregateType());
        assertEquals(15L, savedEvent.getAggregateId());
        assertEquals("OrderCreatedEvent", savedEvent.getEventType());
        assertFalse(savedEvent.isPublished());
        assertEquals("{\"messageId\":\"test-message\"}", savedEvent.getPayload());
    }

    @Test
    void shouldNotSaveOrderWhenReservationFails() {

        CreateOrderRequest request = new CreateOrderRequest(1L, 2);

        when(authentication.getDetails()).thenReturn(
                new AuthenticatedUserDetails(2L, "testuser@gmail.com")
        );

        when(authentication.getName()).thenReturn("testuser");

        when(eventClient.reserveSeats(1L, 2))
                .thenThrow(new RuntimeException("Not enough seats"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.create(request, authentication)
        );

        assertEquals("Not enough seats", exception.getMessage());

        verify(orderRepository, never()).save(any(Order.class));
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
        verifyNoInteractions(orderEventProducer);
    }
}
