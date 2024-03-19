package com.example.ledger.infrastructure.mq.local;

import com.example.ledger.application.command.mq.MqProducerService;
import com.example.ledger.domain.shared.model.Event;
import com.example.ledger.infrastructure.mq.local.event.DataChangeEvent;
import com.example.ledger.infrastructure.mq.local.event.MovementEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

//@Profile("local")
@Service
@RequiredArgsConstructor
public class MqProducerServiceImpl implements MqProducerService {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void sendEventsInTransaction(Runnable runnable, String id, List<Event<?>> events) {
        runnable.run();
        applicationEventPublisher.publishEvent(new DataChangeEvent(this, id, events));
    }

    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void sendMovementInTransaction(Runnable runnable, String id) {
        runnable.run();
        applicationEventPublisher.publishEvent(new MovementEvent(this, id));
    }
}
