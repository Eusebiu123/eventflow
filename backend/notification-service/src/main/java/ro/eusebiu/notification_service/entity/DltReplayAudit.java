package ro.eusebiu.notification_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dlt_replay_audit")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DltReplayAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private Integer partitionId;

    @Column(nullable = false)
    private Long messageOffset;

    private String messageId;

    private Long orderId;

    @Column(nullable = false)
    private String replayedBy;

    @Column(nullable = false)
    private LocalDateTime replayedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReplayStatus status;

    @Column(length = 1000)
    private String errorMessage;
}