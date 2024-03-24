package com.example.ledger.infrastructure.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Getter
public class JwtConfig {
    @Value("${jwt.secret:VGhlc2UgdHdvIHF1ZXN0aW9uIGhhdmUgYmVlbiBjb25mdXNpbmcgbWUgZm9yIGEgd2hpbGUu}")
    private String secret;

    @Value("${jwt.expirationInMin:10}")
    private Integer expirationInMin;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secret.getBytes(), MacAlgorithm.HS256.getName())).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        List<JWK> jwkList = new ArrayList<>();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), MacAlgorithm.HS256.getName());

        jwkList.add(new OctetSequenceKey.Builder(secretKey)
            .build());

        JWKSource<SecurityContext> jwkSource = (jwkSelector, securityContext) -> jwkSelector.select(new JWKSet(jwkList));

        return new NimbusJwtEncoder(jwkSource);
    }
}
