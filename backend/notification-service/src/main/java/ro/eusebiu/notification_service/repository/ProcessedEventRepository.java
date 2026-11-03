package ro.eusebiu.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.eusebiu.notification_service.entity.ProcessedEvent;

import java.util.Optional;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, Long> {

    Optional<ProcessedEvent> findByEventId(String eventId);

    boolean existsByEventId(String eventId);


    @Modifying
    @Query(value = """
            INSERT INTO processed_events
                (event_id, order_id, status, created_at,
                 claim_token, lease_until)
            VALUES
                (:eventId, :orderId, 'PROCESSING',
                 CURRENT_TIMESTAMP, :claimToken,
                 CURRENT_TIMESTAMP + INTERVAL '60 seconds')
            ON CONFLICT (event_id) DO NOTHING
            """, nativeQuery = true)
    int claimEvent(
            @Param("eventId") String eventId,
            @Param("orderId") Long orderId,
            @Param("claimToken") String claimToken
    );

    @Modifying
    @Query(value = """
            UPDATE processed_events
            SET status = 'PROCESSING',
                claim_token = :claimToken,
                lease_until = CURRENT_TIMESTAMP + INTERVAL '60 seconds',
                processed_at = NULL
            WHERE event_id = :eventId
              AND order_id = :orderId
              AND (
                  status = 'FAILED'
                  OR (
                      status = 'PROCESSING'
                      AND lease_until <= CURRENT_TIMESTAMP
                  )
              )
            """, nativeQuery = true)
    int reclaimEvent(
            @Param("eventId") String eventId,
            @Param("orderId") Long orderId,
            @Param("claimToken") String claimToken
    );

    @Modifying
    @Query(value = """
            UPDATE processed_events
            SET status = 'COMPLETED',
                processed_at = CURRENT_TIMESTAMP,
                claim_token = NULL,
                lease_until = NULL
            WHERE event_id = :eventId
              AND claim_token = :claimToken
              AND status = 'PROCESSING'
              AND lease_until > CURRENT_TIMESTAMP
            """, nativeQuery = true)
    int markCompleted(
            @Param("eventId") String eventId,
            @Param("claimToken") String claimToken
    );

    @Modifying
    @Query(value = """
            UPDATE processed_events
            SET status = 'FAILED',
                claim_token = NULL,
                lease_until = NULL
            WHERE event_id = :eventId
              AND claim_token = :claimToken
              AND status = 'PROCESSING'
            """, nativeQuery = true)
    int markFailed(
            @Param("eventId") String eventId,
            @Param("claimToken") String claimToken
    );
}
