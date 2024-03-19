package com.example.ledger.domain.wallet;

import com.example.ledger.domain.account.exception.NoEnoughAmountException;
import com.example.ledger.domain.asset.Asset;
import com.example.ledger.domain.asset.AssetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletTest {

    Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setAssets(new ArrayList<>(List.of(createAsset())));
    }

    @Test
    void deposit_newAsset_add() {
        var asset = createAsset();
        asset.setId("newId");
        wallet.deposit(asset);

        assertEquals(2, wallet.getAssets().size());
    }

    @Test
    void deposit_oldAsset_merge() {
        wallet.deposit(createAsset());

        assertEquals(BigDecimal.valueOf(20), wallet.getAssets().get(0).getAmount());
    }

    @Test
    void froze_notExistsAsset_Throw() {
        var asset = createAsset();
        asset.setId("NotExists");

        assertThrows(NoEnoughAmountException.class, () ->
                wallet.froze(asset));
    }

    @Test
    void unfroze_notExistsAsset_Throw() {
        var asset = createAsset();
        asset.setId("NotExists");

        assertThrows(NoEnoughAmountException.class, () ->
                wallet.unfroze(asset));
    }

    @Test
    void withdraw_notExistsAsset_Throw() {
        var asset = createAsset();
        asset.setId("NotExists");

        assertThrows(NoEnoughAmountException.class, () ->
                wallet.withdraw(asset));
    }

    Asset createAsset() {
        Asset asset = new Asset(AssetType.CURRENCY);
        asset.setId("1");
        asset.setAmount(BigDecimal.valueOf(10));
        asset.setFrozenAmount(BigDecimal.valueOf(5));

        return asset;
    }
}