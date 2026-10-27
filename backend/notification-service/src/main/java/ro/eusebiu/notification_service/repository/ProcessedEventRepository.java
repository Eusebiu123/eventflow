package ro.eusebiu.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.eusebiu.notification_service.entity.ProcessedEvent;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByEventId(String eventId);
}