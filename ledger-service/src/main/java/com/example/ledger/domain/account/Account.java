package com.example.ledger.domain.account;

import com.example.ledger.domain.account.event.*;
import com.example.ledger.domain.account.exception.AccountClosedException;
import com.example.ledger.domain.asset.Asset;
import com.example.ledger.domain.asset.AssetType;
import com.example.ledger.domain.shared.model.AggregateRoot;
import com.example.ledger.domain.shared.model.BusinessException;
import com.example.ledger.domain.wallet.Wallet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Setter
@Getter
public class Account extends AggregateRoot {
    @Getter
    private String accountNumber;

    @Getter
    private String userId;

    private AccountStatus status = AccountStatus.INIT;

    @Getter
    private Map<AssetType, Wallet> wallets = new HashMap<>();
    private static final String ACCOUNT_ID_PREFIX = "A";

    public Account() {
        super(genNewId());
    }

    public Account(String id) {
        super(id);
    }

    public void open(String userId) {
        if (status != AccountStatus.INIT) {
            return;
        }

        var event = new AccountOpenedEvent();
        event.setAccountNumber(id);
        event.setUserId(userId);
        log.debug("open Account: {}", event.getAccountNumber());

        super.apply(event);
    }

    public void close() {
        if (status != AccountStatus.OPEN) {
            return;
        }

        if (getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("can not close Account due to balance >0");
        }

        var event = new AccountClosedEvent();
        super.apply(event);
    }

    public void deposit(Asset asset) {
        validateStatus();

        var event = new AssetDepositEvent();
        event.setAsset(asset);
        super.apply(event);
    }

    public void froze(Asset asset) {
        validateStatus();

        var event = new AssetFrozeEvent();
        event.setAsset(asset);
        super.apply(event);
    }

    public void unfroze(Asset asset) {
        var event = new AssetUnfrozeEvent();
        event.setAsset(asset);
        super.apply(event);
    }

    public void withdraw(Asset asset) {
        var event = new AssetWithdrawEvent();
        event.setAsset(asset);
        super.apply(event);
    }

    public void apply(AccountOpenedEvent event) {
        status = AccountStatus.OPEN;
        accountNumber = event.getAccountNumber();
        userId = event.getUserId();
    }

    public void apply(AccountClosedEvent event) {
        status = AccountStatus.CLOSED;
    }

    public void apply(AssetDepositEvent event) {
        var wallet = getWallet(event.getAsset());
        wallet.deposit(event.getAsset());
    }

    public void apply(AssetFrozeEvent event) {
        var wallet = getWallet(event.getAsset());
        wallet.froze(event.getAsset());
    }

    public void apply(AssetUnfrozeEvent event) {
        var wallet = getWallet(event.getAsset());
        wallet.unfroze(event.getAsset());
    }

    public void apply(AssetWithdrawEvent event) {
        var wallet = getWallet(event.getAsset());
        wallet.withdraw(event.getAsset());
    }

    public Wallet getWallet(Asset asset) {
        return wallets.computeIfAbsent(asset.getAssetType(), k -> new Wallet(asset.getAssetType().toString()));
    }

    public void validateStatus() {
        if (status == AccountStatus.CLOSED) {
            throw new AccountClosedException();
        }
    }

    private BigDecimal getBalance() {
        return wallets.values().stream().map(Wallet::getBalance).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private static String genNewId() {
        // just let account Number and Aggregate id shorter
        return ACCOUNT_ID_PREFIX + UUID.randomUUID().toString().substring(0, 8);
    }
}
