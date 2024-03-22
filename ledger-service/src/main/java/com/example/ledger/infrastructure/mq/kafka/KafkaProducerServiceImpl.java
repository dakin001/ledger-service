package com.example.ledger.infrastructure.mq.kafka;

import com.example.ledger.application.command.mq.MqProducerService;
import com.example.ledger.domain.shared.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@ConditionalOnBean(KafkaConfig.class)
public class KafkaProducerServiceImpl implements MqProducerService {
    private final OutboxProcessor outboxProcessor;
    private final KafkaConfig appConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEventsInTransaction(Runnable businessDbSave, String id, List<Event<?>> events) {
        businessDbSave.run();
        outboxProcessor.saveMsgThenAsyncSend(appConfig.getEventTopic(), id, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMovementInTransaction(Runnable businessDbSave, String id) {
        businessDbSave.run();
        outboxProcessor.saveMsgThenAsyncSend(appConfig.getEventTopic(), id, id);
    }
}
