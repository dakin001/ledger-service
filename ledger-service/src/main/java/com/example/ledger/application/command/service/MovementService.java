package com.example.ledger.application.command.service;

import com.example.ledger.application.command.BatchMovementCommand;

public interface MovementService {
    void createRequest(BatchMovementCommand movementCommand);

    void execute(String id);
}
