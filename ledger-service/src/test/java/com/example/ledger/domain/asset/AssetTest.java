package com.example.ledger.domain.asset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssetTest {
    Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset(AssetType.CURRENCY);
        asset.setAmount(BigDecimal.valueOf(10));
        asset.setFrozenAmount(BigDecimal.valueOf(5));
    }

    @Test
    void deposit_amountIncrease_frozenAmountKeep() {
        asset.deposit(BigDecimal.valueOf(1));

        assertEquals(BigDecimal.valueOf(11), asset.getAmount());
        assertEquals(BigDecimal.valueOf(5), asset.getFrozenAmount());
    }

    @Test
    void froze_amountDecrease_frozenAmountIncrease() {
        asset.froze(BigDecimal.valueOf(1));

        assertEquals(BigDecimal.valueOf(9), asset.getAmount());
        assertEquals(BigDecimal.valueOf(6), asset.getFrozenAmount());
    }

    @Test
    void unfroze_amountIncrease_frozenAmountDecrease() {
        asset.unfroze(BigDecimal.valueOf(1));

        assertEquals(BigDecimal.valueOf(11), asset.getAmount());
        assertEquals(BigDecimal.valueOf(4), asset.getFrozenAmount());
    }

    @Test
    void withdraw_amountKeep_frozenAmountDecrease() {
        asset.withdraw(BigDecimal.valueOf(1));

        assertEquals(BigDecimal.valueOf(10), asset.getAmount());
        assertEquals(BigDecimal.valueOf(4), asset.getFrozenAmount());
    }
}