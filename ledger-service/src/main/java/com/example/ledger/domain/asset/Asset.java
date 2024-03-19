package com.example.ledger.domain.asset;

import com.example.ledger.domain.account.exception.NoEnoughAmountException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Asset {
    private String id;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal frozenAmount = BigDecimal.ZERO;
    private AssetType assetType;

    public Asset(AssetType assetType) {
        this.assetType = assetType;
    }

    public void deposit(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void froze(BigDecimal amount) {
        this.frozenAmount = this.frozenAmount.add(amount);
        this.amount = this.amount.subtract(amount);

        if (this.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NoEnoughAmountException();
        }
    }

    public void unfroze(BigDecimal amount) {
        this.frozenAmount = this.frozenAmount.subtract(amount);
        this.amount = this.amount.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        this.frozenAmount = this.frozenAmount.subtract(amount);

        if (this.frozenAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NoEnoughAmountException();
        }
    }
}
