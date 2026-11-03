
package ro.eusebiu.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.eusebiu.notification_service.entity.ProcessedEvent;
import ro.eusebiu.notification_service.entity.ProcessingStatus;
import ro.eusebiu.notification_service.repository.ProcessedEventRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventClaimService {

    private final ProcessedEventRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String claimEvent(String messageId, Long orderId) {

        String claimToken = UUID.randomUUID().toString();

        int inserted = repository.claimEvent(
                messageId,
                orderId,
                claimToken
        );

        if (inserted == 1) {
            log.info(
                    "New event claimed: messageId={}",
                    messageId
            );
            return claimToken;
        }

        int reclaimed = repository.reclaimEvent(
                messageId,
                orderId,
                claimToken
        );

        if (reclaimed == 1) {
            log.info(
                    "Event reclaimed: messageId={}",
                    messageId
            );
            return claimToken;
        }

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessingStatus getStatus(String messageId) {

        return repository.findByEventId(messageId)
                .map(ProcessedEvent::getStatus)
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(
            String messageId,
            String claimToken
    ) {

        int updated = repository.markCompleted(
                messageId,
                claimToken
        );

        if (updated != 1) {
            throw new IllegalStateException(
                    "Cannot complete event: claim expired or ownership lost. "
                            + messageId
            );
        }

        log.info("Event completed: messageId={}", messageId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(
            String messageId,
            String claimToken
    ) {

        int updated = repository.markFailed(
                messageId,
                claimToken
        );

        if (updated != 1) {
            log.warn(
                    "Cannot mark event as FAILED: claim ownership lost. messageId={}",
                    messageId
            );
        }
    }
}
