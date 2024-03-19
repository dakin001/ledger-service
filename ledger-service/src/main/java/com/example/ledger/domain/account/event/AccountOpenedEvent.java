package com.example.ledger.domain.account.event;

import com.example.ledger.domain.account.Account;
import com.example.ledger.domain.shared.model.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountOpenedEvent extends Event<Account> {
    private String accountNumber;
    private String userId;

    @Override
    protected void accept(Account visitor) {
        visitor.apply(this);
    }
}
