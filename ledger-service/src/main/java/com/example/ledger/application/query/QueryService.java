package com.example.ledger.application.query;

public interface QueryService {
    PageResult<WalletProjection> getBalance(BalanceQueryDto query);
}
