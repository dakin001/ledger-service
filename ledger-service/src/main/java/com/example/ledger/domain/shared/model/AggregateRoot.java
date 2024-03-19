package com.example.ledger.domain.shared.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class AggregateRoot {
    protected String id;
    private int version = 0;

    private final List<Event<?>> changes = new ArrayList<>();

    protected AggregateRoot(String id) {
        this.id = id;
    }

    public void loadsFromHistory(List<Event<?>> history) {
        history.forEach(e -> {
            applyChange(e);
            version += 1;
        });
    }

    protected void apply(Event<?> event) {
        applyChange(event);
        changes.add(event);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void applyChange(Event event) {
        event.accept(this);
    }
}
