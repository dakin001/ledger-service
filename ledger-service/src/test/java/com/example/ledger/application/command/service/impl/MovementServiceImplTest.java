package com.example.ledger.application.command.service.impl;

import com.example.ledger.application.command.BatchMovementCommand;
import com.example.ledger.application.command.service.AccountService;
import com.example.ledger.domain.movement.BatchMovement;
import com.example.ledger.domain.movement.MovementRepository;
import com.example.ledger.domain.shared.AppConfig;
import com.example.ledger.infrastructure.mq.local.MqProducerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovementServiceImplTest {


    @Mock
    MovementRepository movementRepository;
    @Mock
    AccountService accountService;
    @Mock
    AppConfig appConfig;

    @Mock
    ApplicationEventPublisher eventPublisher;
    MovementServiceImpl service;

    @BeforeEach
    public void setUp() {
        service = new MovementServiceImpl(movementRepository, accountService, new MqProducerServiceImpl(eventPublisher));

    }

    @Test
    void createRequest_saveToDatabase() {
        // CASE

        // WHEN
        service.createRequest(new BatchMovementCommand());

        // THEN
        verify(movementRepository, times(1)).save(any(BatchMovement.class));
    }

    @Test
    void execute_finishedStatus_doNothing() {
        // todo
    }
}