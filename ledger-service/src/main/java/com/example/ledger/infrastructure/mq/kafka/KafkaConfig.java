package com.example.ledger.infrastructure.mq.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnExpression("${spring.kafka.enabled:true}")
@Setter
@Getter
public class KafkaConfig {
    @Value("${eventTopic:event}")
    private String eventTopic;

    @Value("${movementTopic:movement}")
    private String movementTopic;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(eventTopic)
                .partitions(2)
                .replicas(1)
                .build();
    }
}
