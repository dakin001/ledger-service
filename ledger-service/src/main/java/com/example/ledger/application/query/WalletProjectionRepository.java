package com.example.ledger.application.query;

import java.util.List;

public interface WalletProjectionRepository {

    List<WalletProjection> getByAccountNumberAndWalletType(BalanceQueryDto query);

    void save(List<WalletProjection> list);
}
