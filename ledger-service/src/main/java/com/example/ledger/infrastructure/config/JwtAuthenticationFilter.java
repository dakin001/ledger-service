package com.example.ledger.infrastructure.config;

import com.example.ledger.domain.account.exception.NoPermissionException;
import com.example.ledger.domain.shared.model.ErrorResponse;
import com.example.ledger.domain.shared.util.JsonSerializationUtils;
import com.example.ledger.domain.shared.util.LoginContextUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnExpression("${securityDisable:false} == false")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${permitUrls:}")
    private List<String> permitUrls;

    private final JwtDecoder jwtDecoder;
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getBearToken(request);

            // for test only
            // token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3lvdXJkb21haW4uY29tIiwic3ViIjoiVTAwMiIsImN1c3RvbV9jbGFpbSI6ImN1c3RvbV9jbGFpbSIsImV4cCI6MjM0MTk5NDI1NSwiaWF0IjoxNzExMjc0MjU1fQ.RXYPevX8ovD4GRuch_BjyanYGc9cknji-8Q09p0WbLo";

            if (StringUtils.isEmpty(token)) {
                // Bearer token missing
                throw new NoPermissionException("Bearer token missing");
            }

            try {
                var jwt = jwtDecoder.decode(token);
                var user = jwt.getSubject();
                LoginContextUtils.setLoginUser(user);
            } catch (Exception ex) {
                throw new NoPermissionException(ex.getMessage());
            }
            filterChain.doFilter(request, response);
        } catch (NoPermissionException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(JsonSerializationUtils.serialize(errorResponse));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        for (String url : permitUrls) {
            if (path.startsWith(url)) {
                return true;
            }
        }

        return false;
    }

    private String getBearToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
            return bearerToken.substring(BEARER_TOKEN_PREFIX.length());
        }
        return null;
    }
}
