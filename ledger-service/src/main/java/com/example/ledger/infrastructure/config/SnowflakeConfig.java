package com.example.ledger.infrastructure.config;

import com.example.ledger.domain.shared.util.IdUtils;
import com.example.ledger.domain.shared.util.SnowflakeIdWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnowflakeConfig {
    private final StringRedisTemplate redisTemplate;

    private static final String SNOWFLAKE_WORK_ID = "snowflake.workId";

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        long dataCenterId;
        long workerId;
        var valueOperations = redisTemplate.opsForValue();
        Long val = valueOperations.increment(SNOWFLAKE_WORK_ID);
        if (val == null) {
            throw new RuntimeException("registerWorker fail");
        }

        val = val % 1024;
        workerId = val & 31;
        dataCenterId = val >> 5;

        var idWorker = new SnowflakeIdWorker(dataCenterId, workerId);
        IdUtils.setSnowflakeIdWorker(idWorker);
        return idWorker;
    }
}
