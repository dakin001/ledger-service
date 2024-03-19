package com.example.ledger.domain.shared.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoginContextUtils {
    private static final String TEST_USER = "U001";

    public static void setLoginUser(Object user) {
// todo
    }

    public static String getCurrentUser() {
        // todo: get real login userId
        return TEST_USER;
    }
}
