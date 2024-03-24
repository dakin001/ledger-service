package com.example.ledger.infrastructure.config;

import com.example.ledger.domain.shared.util.LoginContextUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@ConditionalOnExpression("${securityDisable:false}")
public class SecurityDisableFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // this class dependency config securityDisable=true, it's test only
        LoginContextUtils.setLoginUser("U002");

        filterChain.doFilter(request, response);
    }

}
