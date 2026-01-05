package com.social.network.auth_service.service;

import com.social.network.auth_service.entity.AuthOutboxEventEntity;
import com.social.network.auth_service.entity.OutboxStatus;
import com.social.network.auth_service.repository.AuthOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthOutboxEventService {
    private final AuthOutboxEventRepository repository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuthOutboxEvent(AuthOutboxEventEntity outbox) {
        repository.save(outbox);
    }

    public List<AuthOutboxEventEntity> getPendingOutboxEvents(Pageable pageable) {
        return repository.findPendingForRetry(pageable);
    }
}
