package com.example.ledger.infrastructure.mq.kafka;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository {
    List<OutboxEvent> findAll(LocalDateTime timestamp, int batchSize);

    void save(OutboxEvent event);

    void delete(Long id);
}
