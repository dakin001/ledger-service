package com.example.ledger.domain.shared.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@UtilityClass
public class LoginContextUtils {
    public static void setLoginUser(Object user) {
        var auth = UsernamePasswordAuthenticationToken.authenticated(user,
                null, List.of());
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        return authentication.getName();
    }
}
