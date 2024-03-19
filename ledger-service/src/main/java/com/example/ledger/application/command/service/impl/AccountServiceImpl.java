package com.example.ledger.application.command.service.impl;

import com.example.ledger.application.command.*;
import com.example.ledger.application.command.mq.MqProducerService;
import com.example.ledger.application.command.service.AccountService;
import com.example.ledger.domain.account.Account;
import com.example.ledger.domain.account.AccountMapper;
import com.example.ledger.domain.account.AccountRepository;
import com.example.ledger.domain.account.exception.AccountClosedException;
import com.example.ledger.domain.shared.AppConfig;
import com.example.ledger.domain.shared.annotation.DistributedLock;
import com.example.ledger.domain.shared.annotation.Idempotent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AppConfig appConfig;
    private final MqProducerService mqProducerService;

    @Override
    public Account open(AccountOpenCommand command) {
        Account account = new Account();
        account.open(command.getUserId());

        storeAndPublishEvents(account);
        return account;
    }

    @Override
    public void openSpecialAccount(String accountNumber) {
        if (accountRepository.getById(accountNumber).isPresent()) {
            return;
        }

        Account account = new Account(accountNumber);
        account.open("system");
        storeAndPublishEvents(account);
    }

    @Override
    @DistributedLock(key = "#command.accountId")
    public void close(AccountCloseCommand command) {
        Account account = getById(command.getAccountId());
        // todo: Verify the user is the owner of the account
        account.close();

        storeAndPublishEvents(account);
    }

    @Idempotent(key = "#command.serialNumber")
    @DistributedLock(key = "#command.accountId")
    @Override
    public void deposit(DepositCommand command) {
        Account account = getById(command.getAccountId());
        account.deposit(command.getAsset());

        storeAndPublishEvents(account);
    }

    @Idempotent(key = "#command.serialNumber")
    @DistributedLock(key = "#command.accountId")
    @Override
    public void froze(FrozeCommand command) {
        Account account = getById(command.getAccountId());
        account.froze(command.getAsset());

        storeAndPublishEvents(account);
    }

    @Idempotent(key = "#command.serialNumber")
    @DistributedLock(key = "#command.accountId")
    @Override
    public void unfroze(FrozeCommand command) {
        Account account = getById(command.getAccountId());
        account.unfroze(command.getAsset());

        storeAndPublishEvents(account);
    }

    @Idempotent(key = "#command.serialNumber")
    @DistributedLock(key = "#command.accountId")
    @Override
    public void withdraw(WithdrawCommand command) {
        Account account = getById(command.getAccountId());
        account.withdraw(command.getAsset());

        storeAndPublishEvents(account);
    }

    @Override
    public void checkStatus(String accountNumber) {
        var account = getById(accountNumber);
        if (account != null) {
            account.validateStatus();
        }
    }

    @Override
    public Account getById(String accountNumber) {
        var account = accountRepository.getById(accountNumber).orElseThrow(AccountClosedException::new);

        if (isSpecialAccount(account.getAccountNumber())) {
            return convertToNegativeAccount(account);
        }
        return account;
    }

    private boolean isSpecialAccount(String id) {
        return appConfig.getSpecialEntityAccounts().contains(id);
    }

    private void storeAndPublishEvents(Account account) {
        mqProducerService.sendEventsInTransaction(() -> accountRepository.save(account), account.getId(), account.getChanges());
    }

    private Account convertToNegativeAccount(Account account) {
        return AccountMapper.INSTANCE.convertToNegativeAccount(account);
    }
}
