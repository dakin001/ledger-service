package com.example.ledger.infrastructure.repository.impl;

import com.example.ledger.domain.account.Account;
import com.example.ledger.domain.account.AccountRepository;
import com.example.ledger.infrastructure.repository.EventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {
    private final EventStore eventStore;

    @Override
    public Optional<Account> getById(String id) {
        var events = eventStore.listByAggregateId(id);
        if (events.isEmpty()) {
            return Optional.empty();
        }
        Account account = new Account(id);
        account.loadsFromHistory(events);
        return Optional.of(account);
    }

    @Override
    public void save(Account account) {
        eventStore.save(account);
    }
}
