package com.example.ledger.infrastructure.mq.local;

import com.example.ledger.application.command.mq.MqConsumerService;
import com.example.ledger.infrastructure.mq.local.event.DataChangeEvent;
import com.example.ledger.infrastructure.mq.local.event.MovementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@ConditionalOnBean(LocalConfig.class)
@Component
@RequiredArgsConstructor
@Slf4j
public class LocalEventListener {
    private final MqConsumerService mqConsumerService;

    @EventListener
    public void handleEvent(MovementEvent event) {
        mqConsumerService.receiveMovementMsg(event.getId());
    }

    @EventListener
    public void handleEvent(DataChangeEvent event) {
        mqConsumerService.receiveDataChangeMsg(event.getId(), event.getMessage());
    }
}
