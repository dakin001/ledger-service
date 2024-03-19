package com.example.ledger.domain.movement.event;

import com.example.ledger.domain.movement.BatchMovement;
import com.example.ledger.domain.movement.Movement;
import com.example.ledger.domain.shared.model.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MovementFrozeFailEvent extends Event<BatchMovement> {
    private List<Movement> items;

    @Override
    protected void accept(BatchMovement visitor) {
        visitor.apply(this);
    }
}
