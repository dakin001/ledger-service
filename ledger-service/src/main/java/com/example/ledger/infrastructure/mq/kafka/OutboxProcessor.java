package com.example.ledger.infrastructure.mq.kafka;


import com.example.ledger.domain.shared.util.IdUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    public static final int BATCH_SIZE = 10;
    public static final int MAX_BATCH = 10000;
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 120, timeUnit = TimeUnit.SECONDS)
    public void processAllOutboxEvents() {
        int i = 0;
        while (i++ < MAX_BATCH) {
            if (!processOutboxEvents()) {
                break;
            }
        }
    }

    private boolean processOutboxEvents() {
        List<OutboxEvent> outboxEvents = outboxEventRepository.findAll(LocalDateTime.now(), BATCH_SIZE);
        if (outboxEvents.isEmpty()) {
            return false;
        }

        for (OutboxEvent outboxEvent : outboxEvents) {
            send(outboxEvent);
        }
        return true;
    }

    public void saveMsgThenAsyncSend(String topic, String key, String data) {
        var msg = saveMsg(topic, key, data);
        CompletableFuture.runAsync(() -> send(msg));
    }

    private OutboxEvent saveMsg(String topic, String key, String data) {
        OutboxEvent event = new OutboxEvent();
        event.setId(IdUtils.newId());
        event.setTopic(topic);
        event.setKey(key);
        event.setPayload(data);
        event.setNextRunTime(LocalDateTime.now().plusMinutes(2));

        outboxEventRepository.save(event);
        return event;
    }

    @SneakyThrows
    private void send(OutboxEvent outboxEvent) {
        try {
            // Publish events to Kafka
            kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getKey(), outboxEvent.getPayload())
                    .get(3, TimeUnit.SECONDS);

            // Remove the event from the outbox
            outboxEventRepository.delete(outboxEvent.getId());
        } catch (Exception ex) {
            outboxEvent.setRetryTimes(outboxEvent.getRetryTimes() + 1);
            outboxEvent.setNextRunTime(LocalDateTime.now().plusMinutes((long) Math.pow(3, outboxEvent.getRetryTimes())));
            outboxEvent.setError(StringUtils.substring(ex.getMessage(), 0, 50));
            outboxEventRepository.save(outboxEvent);

            throw ex;
        }
    }
}
