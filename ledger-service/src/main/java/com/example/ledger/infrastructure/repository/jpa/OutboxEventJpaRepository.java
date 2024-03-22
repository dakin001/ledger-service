package com.example.ledger.infrastructure.repository.jpa;

import com.example.ledger.infrastructure.mq.kafka.OutboxEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEvent, Long> {

    @Query("SELECT e FROM OutboxEvent e where e.nextRunTime < :timestamp")
    Page<OutboxEvent> findByNextRunTime(LocalDateTime timestamp, Pageable pageable);
}