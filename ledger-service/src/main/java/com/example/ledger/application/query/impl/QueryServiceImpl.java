package com.example.ledger.application.query.impl;

import com.example.ledger.application.query.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {
    private final WalletProjectionRepository repository;

    @Override
    public PageResult<WalletProjection> getHistory(BalanceQueryDto query) {
        //todo Verify the user is the owner of the account
        var list = repository.getByAccountNumberAndWalletType(query);

        return new PageResult<>(list);
    }
}
