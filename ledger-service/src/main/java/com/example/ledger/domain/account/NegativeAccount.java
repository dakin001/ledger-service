package com.example.ledger.domain.account;

import com.example.ledger.domain.asset.Asset;

import java.math.BigDecimal;

public class NegativeAccount extends Account {
    private final BigDecimal negative = BigDecimal.valueOf(-1);

    @Override
    public void froze(Asset asset) {
        // do nothing
    }

    @Override
    public void unfroze(Asset asset) {
        // do nothing
    }

    @Override
    public void deposit(Asset asset) {
        super.deposit(asset);
    }

    @Override
    public void withdraw(Asset asset) {
        super.deposit(convertToNegativeAsset(asset));
    }

    private Asset convertToNegativeAsset(Asset asset) {
        var negativeAsset = new Asset(asset.getAssetType());
        negativeAsset.setAmount(asset.getAmount().multiply(negative));
        return negativeAsset;
    }
}
