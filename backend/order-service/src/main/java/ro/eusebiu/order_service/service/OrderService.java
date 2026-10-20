package ro.eusebiu.order_service.service;

import org.springframework.security.core.Authentication;
import ro.eusebiu.order_service.dto.request.CreateOrderRequest;
import ro.eusebiu.order_service.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request, Authentication authentication);

    List<OrderResponse> getAllForUser(Long userId);

    OrderResponse getById(Long id);
}