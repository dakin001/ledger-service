package com.example.ledger.domain.movement;

import java.util.Optional;

public interface MovementRepository {
    Optional<BatchMovement> getById(String id);

    void save(BatchMovement movement);
}
