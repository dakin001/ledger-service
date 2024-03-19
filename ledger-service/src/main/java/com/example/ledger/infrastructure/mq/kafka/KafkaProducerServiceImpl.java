package com.example.ledger.infrastructure.mq.kafka;

import com.example.ledger.application.command.mq.MqProducerService;
import com.example.ledger.domain.shared.model.Event;

import java.util.List;

public class KafkaProducerServiceImpl implements MqProducerService {
    //  KafkaTemplate<Object, Object> template;

    @Override
    public void sendEventsInTransaction(Runnable runnable, String id, List<Event<?>> events) {
        // todo:
    }

    @Override
    public void sendMovementInTransaction(Runnable runnable, String id) {
        // todo:
    }
}
