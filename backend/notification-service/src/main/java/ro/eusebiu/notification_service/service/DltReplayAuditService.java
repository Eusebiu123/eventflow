package ro.eusebiu.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.eusebiu.notification_service.entity.DltReplayAudit;
import ro.eusebiu.notification_service.entity.ReplayStatus;
import ro.eusebiu.notification_service.repository.DltReplayAuditRepository;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DltReplayAuditService {

    private final DltReplayAuditRepository auditRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAudit(
            String topic,
            int partitionId,
            long offset,
            String messageId,
            Long orderId,
            String replayedBy,
            ReplayStatus status,
            String errorMessage
    ) {

        DltReplayAudit audit = DltReplayAudit.builder()
                .topic(topic)
                .partitionId(partitionId)
                .messageOffset(offset)
                .messageId(messageId)
                .orderId(orderId)
                .replayedBy(replayedBy)
                .replayedAt(LocalDateTime.now())
                .status(status)
                .errorMessage(
                        errorMessage != null && errorMessage.length() > 1000
                                ? errorMessage.substring(0, 1000)
                                : errorMessage
                )
                .build();

        auditRepository.save(audit);
    }
}