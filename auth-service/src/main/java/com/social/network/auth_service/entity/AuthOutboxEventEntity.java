package com.social.network.auth_service.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auth_outbox_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthOutboxEventEntity extends BaseEntity{
    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(columnDefinition = "json", nullable = false)
    private String payload;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
}
