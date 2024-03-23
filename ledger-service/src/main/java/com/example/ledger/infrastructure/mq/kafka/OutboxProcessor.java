package com.example.ledger.infrastructure.mq.kafka;

public interface OutboxProcessor {
    void saveMsgThenAsyncSend(String topic, String key, String data);
}
