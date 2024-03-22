package com.example.ledger.infrastructure.mq.kafka;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Getter
@Setter
public class OutboxEvent {
    @Id
    private Long id;

    private String topic;

    private String key;

    private String payload;

    // edit > query, so not index here
    private LocalDateTime nextRunTime;

    private Integer retryTimes = 0;
    private String error;
}
