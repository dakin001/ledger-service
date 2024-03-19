package com.example.ledger.domain.movement.event;

import com.example.ledger.domain.movement.BatchMovement;
import com.example.ledger.domain.movement.Movement;
import com.example.ledger.domain.shared.model.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchMovementCreateEvent extends Event<BatchMovement> {
    private List<Movement> movements;

    @Override
    protected void accept(BatchMovement visitor) {
        visitor.apply(this);
    }
}
