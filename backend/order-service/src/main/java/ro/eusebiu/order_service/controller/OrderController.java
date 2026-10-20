package ro.eusebiu.order_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.eusebiu.order_service.dto.request.CreateOrderRequest;
import ro.eusebiu.order_service.dto.response.OrderResponse;
import ro.eusebiu.order_service.service.OrderService;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request,authentication));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();

        return ResponseEntity.ok(orderService.getAllForUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersForUser(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        if (!authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(orderService.getAllForUser(userId));
    }
}