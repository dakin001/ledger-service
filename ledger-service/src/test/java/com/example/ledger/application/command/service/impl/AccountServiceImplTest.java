package com.example.ledger.application.command.service.impl;

import com.example.ledger.application.command.*;
import com.example.ledger.domain.account.Account;
import com.example.ledger.domain.account.AccountRepository;
import com.example.ledger.domain.account.exception.AccountClosedException;
import com.example.ledger.domain.asset.Asset;
import com.example.ledger.domain.asset.AssetType;
import com.example.ledger.domain.shared.AppConfig;
import com.example.ledger.infrastructure.mq.local.LocalProducerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountServiceImplTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    AppConfig appConfig;

    @Mock
    ApplicationEventPublisher eventPublisher;

    AccountServiceImpl service;
    Account account;
    Asset testAsset;

    @BeforeEach
    public void setUp() {
        service = new AccountServiceImpl(accountRepository, appConfig, new LocalProducerServiceImpl(eventPublisher));

        testAsset = new Asset(AssetType.CURRENCY);
        testAsset.setId("1");
        testAsset.setAmount(BigDecimal.valueOf(10));
        testAsset.setFrozenAmount(BigDecimal.valueOf(10));

        account = new Account();
        account.setId("acct1");
        account.setAccountNumber(account.getId());
        account.setWallets(new HashMap<>());
        account.getWallet(testAsset).getAssets().add(testAsset);
        when(accountRepository.getById(account.getId())).thenReturn(Optional.of(account));
    }

    @Test
    void open_saveToDatabase() {
        // CASE
        var command = new AccountOpenCommand();

        // WHEN
        service.open(command);

        // THEN
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void close_accountExists_saveToDatabase() {
        // CASE
        var command = new AccountCloseCommand();
        command.setAccountId(account.getId());

        // WHEN
        service.close(command);

        // THEN
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void open_openSpecialAccount_saveToDatabase() {
        // CASE

        // WHEN
        service.openSpecialAccount("A00000000");

        // THEN
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void close_specialAccount_saveToDatabase() {
        // CASE
        var command = new AccountCloseCommand();
        command.setAccountId(account.getId());
        when(appConfig.getSpecialEntityAccounts()).thenReturn(List.of(account.getId()));

        // WHEN
        service.close(command);

        // THEN
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void close_accountNotExists_throw() {
        // CASE
        var command = new AccountCloseCommand();
        command.setAccountId("notExitsId");

        // WHEN THEN
        assertThrows(AccountClosedException.class, () -> service.close(command));
    }

    @Test
    void deposit_saveToDatabase() {
        // CASE
        var command = new DepositCommand();
        command.setAccountId(account.getId());
        command.setAsset(testAsset);

        // WHEN
        service.deposit(command);

        // THEN
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void froze_saveToDatabase() {
        // CASE
        var command = new FrozeCommand();
        command.setAccountId(account.getId());
        command.setAsset(testAsset);

        // WHEN
        service.froze(command);

        // THEN
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void unfroze_saveToDatabase() {
        // CASE
        var command = new FrozeCommand();
        command.setAccountId(account.getId());
        command.setAsset(testAsset);

        // WHEN
        service.unfroze(command);

        // THEN
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void withdraw_saveToDatabase() {
        // CASE
        var command = new WithdrawCommand();
        command.setAccountId(account.getId());
        command.setAsset(testAsset);

        // WHEN
        service.withdraw(command);

        // THEN
        verify(accountRepository, times(1)).save(any(Account.class));
    }
}