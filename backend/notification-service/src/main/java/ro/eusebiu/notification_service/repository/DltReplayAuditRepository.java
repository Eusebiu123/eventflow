package ro.eusebiu.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.eusebiu.notification_service.entity.DltReplayAudit;

public interface DltReplayAuditRepository
        extends JpaRepository<DltReplayAudit, Long> {
}