package ro.eusebiu.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.eusebiu.order_service.entity.Order;
import ro.eusebiu.order_service.entity.OrderStatus;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

}
