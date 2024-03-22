package com.example.ledger.infrastructure.mq.kafka.event;

import com.example.ledger.domain.shared.model.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataChangeEvent {
    private String id;
    private List<Event<?>> changes;
}
