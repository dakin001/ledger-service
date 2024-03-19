package com.example.ledger.application.command.service.impl;

import com.example.ledger.application.command.BatchMovementCommand;
import com.example.ledger.application.command.mapper.MovementCommandMapper;
import com.example.ledger.application.command.mq.MqProducerService;
import com.example.ledger.application.command.service.AccountService;
import com.example.ledger.application.command.service.MovementService;
import com.example.ledger.domain.movement.BatchMovement;
import com.example.ledger.domain.movement.MovementRepository;
import com.example.ledger.domain.shared.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {
    private final MovementRepository movementRepository;
    private final AccountService accountService;

    private final MqProducerService mqProducerService;

    @Override
    public void createRequest(BatchMovementCommand movementCommand) {
        // todo: Verify the user is the owner of the source account
        BatchMovement movement = getBatchMovement(movementCommand);
        // just create it, then use async handle it.
        storeAndPublishEvents(movement);
    }

    @DistributedLock(key = "#id")
    @Override
    public void execute(String id) {
        var movement = movementRepository.getById(id)
                .orElse(null);
        if (movement == null || movement.isFinalStatus()) {
            return;
        }

        movement.setAccountService(accountService);
        // Eventual consistency, this method may run multiple times by MQ trigger
        movement.execute();
        movementRepository.save(movement);
    }

    private BatchMovement getBatchMovement(BatchMovementCommand movementCommand) {
        var movements = MovementCommandMapper.INSTANCE.toMovement(movementCommand.getDetails());
        return new BatchMovement(movements);
    }

    private void storeAndPublishEvents(BatchMovement movement) {
        if (!movement.isFinalStatus()) {
            mqProducerService.sendMovementInTransaction(() -> movementRepository.save(movement),
                    movement.getId());
            return;
        }

        movementRepository.save(movement);
    }
}
