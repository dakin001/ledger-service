package com.example.ledger.domain.account;

import com.example.ledger.domain.account.event.*;
import com.example.ledger.domain.account.exception.AccountClosedException;
import com.example.ledger.domain.asset.Asset;
import com.example.ledger.domain.asset.AssetSnapshot;
import com.example.ledger.domain.asset.AssetType;
import com.example.ledger.domain.shared.model.BusinessException;
import com.example.ledger.domain.shared.model.Event;
import com.example.ledger.domain.wallet.WalletSnapshot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    @Test
    void open_success() {
        // CASE
        Account account = new Account();

        //  WHEN
        account.open("acct1");

        //  THEN
        assertEquals(AccountOpenedEvent.class, account.getChanges().get(0).getClass());
    }

    @Test
    void close_noAsset_success() {
        // CASE
        Account account = createTestAccount();

        // CASE
        account.close();

        // THEN
        assertEquals(AccountClosedEvent.class, account.getChanges().get(0).getClass());
    }

    @Test
    void close_hasAsset_fail() {
        // CASE
        Account account = createTestAccountWithAsset(UUID.randomUUID().toString(), BigDecimal.valueOf(10));

        // WHEN THEN
        assertThrows(BusinessException.class, account::close);
    }

    @Test
    void deposit_openAccount_success() {
        // CASE
        var asset = createTestAsset(BigDecimal.valueOf(10));
        Account account = createTestAccount();

        // WHEN
        account.deposit(asset);

        // THEN
        assertEquals(AssetDepositEvent.class, account.getChanges().get(0).getClass());
    }

    @Test
    void deposit_closeAccount_fail() {
        // CASE
        var asset = createTestAsset(BigDecimal.valueOf(10));
        Account account = createTestAccount();
        account.close();

        // WHEN THEN
        assertThrows(AccountClosedException.class,
                () -> account.deposit(asset));
    }

    @Test
    void froze_enoughBalance_success() {
        // CASE
        var existsAssetId = UUID.randomUUID().toString();
        Account account = createTestAccountWithAsset(existsAssetId, BigDecimal.valueOf(10));
        var asset = createTestAsset(BigDecimal.valueOf(10));
        asset.setId(existsAssetId);

        // WHEN
        account.froze(asset);

        // THEN
        assertEquals(AssetFrozeEvent.class, account.getChanges().get(0).getClass());
    }

    @Test
    void unfroze_enoughBalance_success() {
        // CASE
        var existsAssetId = UUID.randomUUID().toString();
        Account account = createTestAccountWithAsset(existsAssetId, BigDecimal.valueOf(10));
        var asset = createTestAsset(BigDecimal.valueOf(10));
        asset.setId(existsAssetId);
        account.froze(asset);

        // WHEN
        account.unfroze(asset);

        // THEN
        assertEquals(AssetUnfrozeEvent.class, account.getChanges().get(1).getClass());
    }

    @Test
    void withdraw_enoughFrozeAmount_success() {
        // CASE
        var existsAssetId = UUID.randomUUID().toString();
        AccountSnapshot snapshot = createTestAccountSnapshot(existsAssetId, BigDecimal.valueOf(10));
        Account account = AccountMapper.INSTANCE.fromSnapshot(snapshot);
        var asset = createTestAsset(BigDecimal.valueOf(10));
        asset.setId(existsAssetId);

        // WHEN
        account.withdraw(asset);

        // THEN
        assertEquals(AssetWithdrawEvent.class, account.getChanges().get(0).getClass());
    }

    Asset createTestAsset(BigDecimal amount) {
        var asset = new Asset(AssetType.CURRENCY);
        asset.setId(UUID.randomUUID().toString());
        asset.setAmount(amount);
        return asset;
    }

    Account createTestAccount() {
        return createTestAccountWithAsset(null, null);
    }

    Account createTestAccountWithAsset(String assetId, BigDecimal amount) {
        var openEvent = new AccountOpenedEvent();
        openEvent.setAccountNumber("001");

        List<Event<?>> events = new ArrayList<>();
        events.add(openEvent);

        if (assetId != null) {
            var asset = new Asset(AssetType.CURRENCY);
            asset.setId(assetId);
            asset.setAmount(amount);
            AssetDepositEvent depositEvent = new AssetDepositEvent();
            depositEvent.setAsset(asset);

            events.add(depositEvent);
        }
        var account = new Account();

        account.loadsFromHistory(events);
        return account;
    }

    AccountSnapshot createTestAccountSnapshot(String assetId, BigDecimal amount) {
        var account = new AccountSnapshot();
        account.setId("acct1");
        account.setVersion(1);
        account.setAccountNumber("acct1");
        account.setUserId("user1");
        account.setStatus(AccountStatus.OPEN);
        account.setWallets(new HashMap<>());

        if (assetId != null) {
            var asset = new AssetSnapshot();
            asset.setAssetType(AssetType.CURRENCY);
            asset.setId(assetId);
            asset.setAmount(amount);
            asset.setFrozenAmount(amount);

            var wallet = new WalletSnapshot();
            wallet.setBalance(amount);
            wallet.setAssets(List.of(asset));
            account.getWallets().put(asset.getAssetType(), wallet);
        }

        return account;
    }
}