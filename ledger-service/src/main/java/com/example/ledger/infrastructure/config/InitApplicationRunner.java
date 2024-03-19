package com.example.ledger.infrastructure.config;

import com.example.ledger.application.command.service.AccountService;
import com.example.ledger.domain.shared.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitApplicationRunner implements ApplicationRunner {
    private final AccountService accountService;
    private final AppConfig appConfig;

    @Override
    public void run(ApplicationArguments args) {
        for (var accountNumber : appConfig.getSpecialEntityAccounts()) {
            accountService.openSpecialAccount(accountNumber);
        }
        for (var accountNumber : appConfig.getTestAccounts()) {
            accountService.openSpecialAccount(accountNumber);
        }
    }
}
