package com.example.ledger.domain.account.event;

import com.example.ledger.domain.account.Account;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetWithdrawEvent extends BaseAssetEvent<Account> {

    @Override
    protected void accept(Account visitor) {
        visitor.apply(this);
    }
}