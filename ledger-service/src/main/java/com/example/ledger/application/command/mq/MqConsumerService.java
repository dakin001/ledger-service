package com.example.ledger.application.command.mq;

import com.example.ledger.domain.shared.model.Event;

import java.util.List;

public interface MqConsumerService {

    void receiveMovementMsg(String id);

    void receiveDataChangeMsg(String id, List<Event<?>> events);
}
