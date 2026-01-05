package com.social.network.auth_service.controller;

import com.social.network.auth_service.entity.AuthOutboxEventEntity;
import com.social.network.auth_service.entity.OutboxStatus;
import com.social.network.auth_service.repository.AuthOutboxEventRepository;
import com.social.network.auth_service.service.AuthOutboxEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRetryScheduler {

    private final AuthOutboxEventService authOutboxEventService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final int BATCH_SIZE = 50;
    private static final int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void retryFailedOutboxEvents() {
        log.info("Start run schedule scan auth_outbox_event table");
        List<AuthOutboxEventEntity> failedEvents =
                authOutboxEventService.getPendingOutboxEvents((Pageable) PageRequest.of(0, BATCH_SIZE));
        for (AuthOutboxEventEntity event : failedEvents) {
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(event.getEventType().getValue(),
                            event.getAggregateId(),
                            event.getPayload());

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    // Kafka gửi thất bại
                    int retry = event.getRetryCount() + 1;
                    if (retry >= MAX_RETRY) {
                        event.setStatus(OutboxStatus.DLQ);
                        log.error("Kafka event moved to DLQ, id={}, error={}", event.getId(), ex.getMessage());
                    } else {
                        event.setStatus(OutboxStatus.SEND_FAILED);
                        log.warn("Kafka retry failed, id={}, retryCount={}, error={}", event.getId(), retry, ex.getMessage());
                    }
                    event.setRetryCount(retry);
                } else {
                    // Kafka gửi thành công
                    event.setStatus(OutboxStatus.SENT);
                    event.setRetryCount(event.getRetryCount() + 1);
                    log.info("Kafka event sent successfully, id={}", event.getId());
                }
                authOutboxEventService.saveAuthOutboxEvent(event);
            });

        }
    }
}



