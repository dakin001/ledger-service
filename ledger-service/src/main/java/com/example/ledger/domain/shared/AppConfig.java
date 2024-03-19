package com.example.ledger.domain.shared;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
@Setter
public class AppConfig {
    @Value("${specialEntityAccounts:}")
    private List<String> specialEntityAccounts;
    @Value("${testAccounts:}")
    private List<String> testAccounts;
}
