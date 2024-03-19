package com.example.ledger.infrastructure.mq.local.event;

import com.example.ledger.domain.shared.model.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class DataChangeEvent extends ApplicationEvent {
    private final List<Event<?>> message;
    private final String id;

    public DataChangeEvent(Object source, String id, List<Event<?>> message) {
        super(source);
        this.message = message;
        this.id = id;
    }
}
