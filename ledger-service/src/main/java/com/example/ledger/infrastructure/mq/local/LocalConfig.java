package com.example.ledger.infrastructure.mq.local;

import com.example.ledger.infrastructure.mq.kafka.KafkaConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(KafkaConfig.class)
@Setter
@Getter
public class LocalConfig {
}
