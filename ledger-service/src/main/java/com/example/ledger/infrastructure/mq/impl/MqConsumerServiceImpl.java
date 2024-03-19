package com.example.ledger.infrastructure.mq.impl;

import com.example.ledger.application.command.mq.MqConsumerService;
import com.example.ledger.application.command.service.MovementService;
import com.example.ledger.application.query.WalletProjectionService;
import com.example.ledger.domain.shared.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MqConsumerServiceImpl implements MqConsumerService {
    private final MovementService movementService;
    private final WalletProjectionService walletProjectionService;

    @Override
    public void receiveMovementMsg(String id) {
        movementService.execute(id);
    }

    @Override
    public void receiveDataChangeMsg(String id, List<Event<?>> events) {
        walletProjectionService.createProjection(id);
    }
}
