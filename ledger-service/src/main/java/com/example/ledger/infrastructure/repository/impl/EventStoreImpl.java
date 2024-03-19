package com.example.ledger.infrastructure.repository.impl;

import com.example.ledger.domain.shared.model.AggregateRoot;
import com.example.ledger.domain.shared.model.Event;
import com.example.ledger.domain.shared.util.IdUtils;
import com.example.ledger.domain.shared.util.KryoSerializationUtils;
import com.example.ledger.infrastructure.repository.EventDbModel;
import com.example.ledger.infrastructure.repository.EventStore;
import com.example.ledger.infrastructure.repository.jpa.EventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventStoreImpl implements EventStore {
    private final EventJpaRepository repository;

    @Override
    @jakarta.transaction.Transactional // for Large Objects may not be used in auto-commit mode.
    public List<Event<?>> listByAggregateId(String aggregateId) {
        var data = repository.getByAggregateId(aggregateId);

        List<Event<?>> result = new ArrayList<>();
        for (var item : data) {
            result.add((Event<?>) toEvent(item));
        }
        return result;
    }

    @Override
    public void save(AggregateRoot aggregateRoot) {
        var data = aggregateRoot.getChanges().stream().map(x -> toEventDbModel(aggregateRoot, x)).toList();

        repository.saveAll(data);
    }

    private EventDbModel toEventDbModel(AggregateRoot aggregate, Event<?> event) {
        EventDbModel model = new EventDbModel();
        model.setId(IdUtils.newId());
        model.setAggregateId(aggregate.getId());
        model.setVersion(aggregate.getVersion());
        model.setData(KryoSerializationUtils.serialize(event));
        model.setName(event.getClass().getSimpleName());

        return model;
    }

    private Object toEvent(EventDbModel model) {
        return KryoSerializationUtils.deserialize(model.getData());
    }
}
