package com.example.ledger.domain.shared.model;

public abstract class Event<T extends AggregateRoot> {

    protected abstract void accept(T visitor);
}
