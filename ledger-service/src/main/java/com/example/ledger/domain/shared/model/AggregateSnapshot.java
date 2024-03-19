package com.example.ledger.domain.shared.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AggregateSnapshot {
    private String id;
    private Integer version;
}
