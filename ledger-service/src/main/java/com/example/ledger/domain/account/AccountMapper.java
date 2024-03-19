package com.example.ledger.domain.account;

import com.example.ledger.domain.asset.Asset;
import com.example.ledger.domain.asset.AssetSnapshot;
import com.example.ledger.domain.wallet.Wallet;
import com.example.ledger.domain.wallet.WalletSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountSnapshot toSnapshot(Account entity);

    AssetSnapshot toSnapshot(Asset entity);

    WalletSnapshot toSnapshot(Wallet entity);

    @Mapping(target = "changes", ignore = true)
    Account fromSnapshot(AccountSnapshot snapshot);

    Asset fromSnapshot(AssetSnapshot snapshot);

    Wallet fromSnapshot(WalletSnapshot snapshot);

    @Mapping(target = "changes", ignore = true)
    NegativeAccount convertToNegativeAccount(AccountSnapshot snapshot);

    default NegativeAccount convertToNegativeAccount(Account account) {
        return convertToNegativeAccount(toSnapshot(account));
    }
}
