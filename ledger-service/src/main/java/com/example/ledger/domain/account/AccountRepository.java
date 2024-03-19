package com.example.ledger.domain.account;

import java.util.Optional;

public interface AccountRepository {

    Optional<Account> getById(String id);

    void save(Account account);
}
