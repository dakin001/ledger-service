package com.example.ledger.infrastructure.repository.impl;

import com.example.ledger.domain.movement.BatchMovement;
import com.example.ledger.domain.movement.MovementRepository;
import com.example.ledger.infrastructure.repository.EventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MovementRepositoryImpl implements MovementRepository {
    private final EventStore eventStore;

    @Override
    public Optional<BatchMovement> getById(String id) {
        var events = eventStore.listByAggregateId(id);

        if (events.isEmpty()) {
            return Optional.empty();
        }
        BatchMovement movement = new BatchMovement(id);
        movement.loadsFromHistory(events);
        return Optional.of(movement);
    }

    @Override
    public void save(BatchMovement batchMovement) {
        eventStore.save(batchMovement);
    }
}
