package com.example.ledger.domain.movement.event;

import com.example.ledger.domain.movement.BatchMovement;
import com.example.ledger.domain.shared.model.Event;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MovementFailEvent extends Event<BatchMovement> {
    @Override
    protected void accept(BatchMovement visitor) {
        visitor.apply(this);
    }
}
