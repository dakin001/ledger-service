package com.example.ledger.domain.account;

import com.example.ledger.domain.asset.AssetType;
import com.example.ledger.domain.shared.model.AggregateSnapshot;
import com.example.ledger.domain.wallet.WalletSnapshot;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class AccountSnapshot extends AggregateSnapshot {

    private String accountNumber;

    private String userId;

    private AccountStatus status;

    private Map<AssetType, WalletSnapshot> wallets = new HashMap<>();
}
