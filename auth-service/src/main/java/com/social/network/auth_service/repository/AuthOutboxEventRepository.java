package com.social.network.auth_service.repository;

import com.social.network.auth_service.entity.AuthOutboxEventEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthOutboxEventRepository extends JpaRepository<AuthOutboxEventEntity,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select e from AuthOutboxEventEntity e
        where e.status = 'SEND_FAILED' and e.retryCount < 5
        order by e.createdAt
    """)
    List<AuthOutboxEventEntity> findPendingForRetry(Pageable pageable);
}

