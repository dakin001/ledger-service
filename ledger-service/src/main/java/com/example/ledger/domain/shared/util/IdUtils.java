package com.example.ledger.domain.shared.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IdUtils {
    private static SnowflakeIdWorker idWorker;

    public void setSnowflakeIdWorker(SnowflakeIdWorker snowflakeIdWorker) {
        idWorker = snowflakeIdWorker;
    }

    public static Long newId() {
        return idWorker.nextId();
    }
}
