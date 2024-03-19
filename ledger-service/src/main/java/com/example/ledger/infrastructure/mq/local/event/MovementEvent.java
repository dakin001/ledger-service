package com.example.ledger.infrastructure.mq.local.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MovementEvent extends ApplicationEvent {
    private final String id;

    public MovementEvent(Object source, String message) {
        super(source);
        this.id = message;
    }
}
