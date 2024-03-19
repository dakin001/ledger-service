package com.example.ledger.domain.wallet;

import com.example.ledger.domain.account.exception.NoEnoughAmountException;
import com.example.ledger.domain.asset.Asset;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Getter
@Setter
public class Wallet {
    private String walletType;
    private List<Asset> assets = new ArrayList<>();

    public Wallet() {
    }

    public Wallet(String walletType) {
        this.walletType = walletType;
    }

    public BigDecimal getBalance() {
        return assets.stream().map(Asset::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public void deposit(Asset asset) {
        var target = findExistsAsset(asset.getId())
                .orElse(null);
        if (target == null) {
            assets.add(asset);
            return;
        }

        target.deposit(asset.getAmount());
    }

    public void froze(Asset req) {
        var target = findExistsAsset(req.getId())
                .orElseThrow(NoEnoughAmountException::new);

        target.froze(req.getAmount());
    }

    public void unfroze(Asset req) {
        var target = findExistsAsset(req.getId())
                .orElseThrow(NoEnoughAmountException::new);

        target.unfroze(req.getAmount());
    }

    public void withdraw(Asset req) {
        var target = findExistsAsset(req.getId())
                .orElseThrow(NoEnoughAmountException::new);

        target.withdraw(req.getAmount());
    }

    private Optional<Asset> findExistsAsset(String id) {
        return assets.stream().filter(x -> Objects.equals(id, x.getId()))
                .findFirst();
    }
}
