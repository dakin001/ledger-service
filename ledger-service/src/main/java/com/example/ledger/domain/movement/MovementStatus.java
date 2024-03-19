package com.example.ledger.domain.movement;

public enum MovementStatus {
    PENDING,
    PROCESSING,
    PROCESSING_ROLLBACK,
    CLEARED,
    FAILED
}
