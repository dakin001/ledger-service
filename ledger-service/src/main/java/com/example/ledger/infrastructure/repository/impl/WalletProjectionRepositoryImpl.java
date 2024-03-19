package com.example.ledger.infrastructure.repository.impl;

import com.example.ledger.application.query.BalanceQueryDto;
import com.example.ledger.application.query.WalletProjection;
import com.example.ledger.application.query.WalletProjectionRepository;
import com.example.ledger.infrastructure.repository.jpa.ProjectionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WalletProjectionRepositoryImpl implements WalletProjectionRepository {
    private final ProjectionJpaRepository repository;

    @Override
    public List<WalletProjection> getByAccountNumberAndWalletType(BalanceQueryDto query) {
        return repository.getByAccountNumberAndWalletType(query.getAccountNumber(), query.getWalletType());
    }

    @Override
    public void save(List<WalletProjection> list) {
        repository.saveAll(list);
    }
}
