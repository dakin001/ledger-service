package com.example.ledger.domain.wallet;

import com.example.ledger.domain.asset.AssetSnapshot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class WalletSnapshot {
    private String walletType;
    private BigDecimal balance;
    private List<AssetSnapshot> assets;
}
