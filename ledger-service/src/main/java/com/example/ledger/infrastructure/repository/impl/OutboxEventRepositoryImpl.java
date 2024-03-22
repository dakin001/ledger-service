package com.example.ledger.infrastructure.repository.impl;

import com.example.ledger.infrastructure.mq.kafka.OutboxEvent;
import com.example.ledger.infrastructure.mq.kafka.OutboxEventRepository;
import com.example.ledger.infrastructure.repository.jpa.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxEventRepositoryImpl implements OutboxEventRepository {
    private final OutboxEventJpaRepository repository;

    @Override
    public List<OutboxEvent> findAll(LocalDateTime timestamp, int batchSize) {
        return repository.findByNextRunTime(timestamp, PageRequest.of(0, batchSize))
                .toList();
    }

    @Override
    public void save(OutboxEvent event) {
        repository.save(event);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
