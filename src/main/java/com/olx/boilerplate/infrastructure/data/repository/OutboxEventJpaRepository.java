package com.olx.boilerplate.infrastructure.data.repository;

import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventData, Long> {

    /**
     * Claims unpublished events with {@code FOR UPDATE SKIP LOCKED} so multiple relay instances do not process the same rows
     * (at-least-once delivery; consumers must be idempotent).
     */
    @Query(value = "SELECT * FROM outbox_event WHERE published = false ORDER BY created_at ASC LIMIT :limit FOR UPDATE SKIP LOCKED",
                    nativeQuery = true)
    List<OutboxEventData> findUnpublishedForUpdate(@Param("limit") int limit);
}
