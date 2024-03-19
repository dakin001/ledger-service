package com.example.ledger.domain.shared.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IdUtils {
    public static Long newId() {
        // todo: replace to snowflake
        return System.currentTimeMillis();
    }
}
