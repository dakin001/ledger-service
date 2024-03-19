package com.example.ledger.application.query.impl;

import com.example.ledger.application.query.WalletProjection;
import com.example.ledger.application.query.WalletProjectionRepository;
import com.example.ledger.application.query.WalletProjectionService;
import com.example.ledger.domain.account.AccountMapper;
import com.example.ledger.domain.account.AccountRepository;
import com.example.ledger.domain.account.AccountSnapshot;
import com.example.ledger.domain.shared.util.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletProjectionServiceImpl implements WalletProjectionService {
    private final WalletProjectionRepository repository;
    private final AccountRepository accountRepository;

    @Override
    public void createProjection(String id) {
        var account = accountRepository.getById(id).orElseThrow();
        AccountSnapshot snapshot = AccountMapper.INSTANCE.toSnapshot(account);

        List<WalletProjection> newProjections = getNewProjections(snapshot);
        repository.save(newProjections);
    }

    private List<WalletProjection> getNewProjections(AccountSnapshot account) {
        List<WalletProjection> result = new ArrayList<>();
        for (var snapWallet : account.getWallets().values()) {
            WalletProjection projection = new WalletProjection();
            projection.setId(IdUtils.newId());
            projection.setUserId(account.getUserId());
            projection.setAccountNumber(account.getAccountNumber());
            projection.setWalletType(snapWallet.getWalletType());
            projection.setBalance(snapWallet.getBalance());
            projection.setVersion(account.getVersion());
            projection.setTimestamp(LocalDateTime.now());
            projection.setUserId(account.getUserId());

            result.add(projection);
        }
        return result;
    }
}
