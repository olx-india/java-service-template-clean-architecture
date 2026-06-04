package com.olx.boilerplate.infrastructure.data.repository;

import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventData, Long> {

    List<OutboxEventData> findByPublishedFalseOrderByCreatedAtAsc(Pageable pageable);
}
