package com.example.ledger.infrastructure.mq.local;

import com.example.ledger.application.command.mq.MqConsumerService;
import com.example.ledger.infrastructure.mq.local.event.DataChangeEvent;
import com.example.ledger.infrastructure.mq.local.event.MovementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//@Profile("local")
@Component
@RequiredArgsConstructor
@Slf4j
public class LocalEventListener {
    private final MqConsumerService mqConsumerService;

    @EventListener
    public void handleEvent(MovementEvent event) {
        try {
            mqConsumerService.receiveMovementMsg(event.getId());
        } catch (Exception ex) {
            log.error("local Consume exception {}", ex.getMessage());
        }
    }

    @EventListener
    public void handleEvent(DataChangeEvent event) {
        try {
            mqConsumerService.receiveDataChangeMsg(event.getId(), event.getMessage());
        } catch (Exception ex) {
            log.error("local Consume exception {}", ex.getMessage());
        }
    }
}
