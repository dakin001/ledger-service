package com.example.ledger.infrastructure.mq.kafka;

import com.example.ledger.application.command.mq.MqProducerService;
import com.example.ledger.domain.shared.model.Event;
import com.example.ledger.domain.shared.util.JsonSerializationUtils;
import com.example.ledger.infrastructure.mq.kafka.event.DataChangeEvent;
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
    public void storeAndPublishAccountEvents(Runnable businessDataSave, String id, List<Event<?>> events) {
        businessDataSave.run();
        var data = JsonSerializationUtils.serialize(new DataChangeEvent(id, null));
        outboxProcessor.saveMsgThenAsyncSend(appConfig.getEventTopic(), id, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storeAndPublishMovementEvents(Runnable businessDataSave, String id) {
        businessDataSave.run();
        var data = JsonSerializationUtils.serialize(new DataChangeEvent(id, null));
        outboxProcessor.saveMsgThenAsyncSend(appConfig.getMovementTopic(), id, data);
    }
}
