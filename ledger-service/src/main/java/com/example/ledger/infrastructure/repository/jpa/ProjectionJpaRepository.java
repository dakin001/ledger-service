package com.example.ledger.infrastructure.repository.jpa;

import com.example.ledger.application.query.WalletProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectionJpaRepository extends JpaRepository<WalletProjection, Long> {

    List<WalletProjection> getByAccountNumberAndWalletType(String accountNumber, String walletType);
}
