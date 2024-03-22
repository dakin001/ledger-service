package com.example.ledger.infrastructure.mq.local;

import com.example.ledger.application.command.mq.MqProducerService;
import com.example.ledger.domain.shared.model.Event;
import com.example.ledger.infrastructure.mq.local.event.DataChangeEvent;
import com.example.ledger.infrastructure.mq.local.event.MovementEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ConditionalOnBean(LocalConfig.class)
@Service
@RequiredArgsConstructor
public class LocalProducerServiceImpl implements MqProducerService {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storeAndPublishAccountEvents(Runnable runnable, String id, List<Event<?>> events) {
        runnable.run();
        applicationEventPublisher.publishEvent(new DataChangeEvent(this, id, events));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storeAndPublishMovementEvents(Runnable runnable, String id) {
        runnable.run();
        applicationEventPublisher.publishEvent(new MovementEvent(this, id));
    }
}
