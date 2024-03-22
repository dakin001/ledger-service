package com.example.ledger.application.command.mq;

import com.example.ledger.domain.shared.model.Event;

import java.util.List;

public interface MqProducerService {
    void storeAndPublishAccountEvents(Runnable runnable, String id, List<Event<?>> events);

    void storeAndPublishMovementEvents(Runnable runnable, String id);
}

