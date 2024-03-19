package com.example.ledger.infrastructure.repository;

import com.example.ledger.domain.shared.model.AggregateRoot;
import com.example.ledger.domain.shared.model.Event;

import java.util.List;

public interface EventStore {
    List<Event<?>> listByAggregateId(String aggregateId);

    void save(AggregateRoot aggregateRoot);
}
