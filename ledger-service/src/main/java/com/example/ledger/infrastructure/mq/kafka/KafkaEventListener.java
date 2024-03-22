package com.example.ledger.infrastructure.mq.kafka;

import com.example.ledger.application.command.mq.MqConsumerService;
import com.example.ledger.infrastructure.mq.kafka.event.DataChangeEvent;
import com.example.ledger.infrastructure.mq.local.LocalConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@ConditionalOnBean(LocalConfig.class)
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListener {
    private final MqConsumerService mqConsumerService;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 60_000, maxDelay = 600_000, multiplier = 3))
    @KafkaListener(id = "${spring.application.name}-event", groupId = "${spring.application.name}", topics = "${eventTopic:event}")
    public void listen(DataChangeEvent event) {
        mqConsumerService.receiveDataChangeMsg(event.getId(), event.getChanges());
    }

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 60_000, maxDelay = 600_000, multiplier = 3))
    @KafkaListener(id = "${spring.application.name}-movement", groupId = "${spring.application.name}", topics = "${eventTopic:movement}")
    public void listenMovement(DataChangeEvent event) {
        mqConsumerService.receiveMovementMsg(event.getId());
    }
}
