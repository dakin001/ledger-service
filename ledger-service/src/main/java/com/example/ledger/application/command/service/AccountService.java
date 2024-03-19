package com.example.ledger.application.command.service;

import com.example.ledger.application.command.*;
import com.example.ledger.domain.account.Account;

public interface AccountService {
    Account open(AccountOpenCommand command);

    void openSpecialAccount(String accountNumber);

    void close(AccountCloseCommand command);

    void deposit(DepositCommand command);

    void froze(FrozeCommand command);

    void unfroze(FrozeCommand command);

    void withdraw(WithdrawCommand command);

    void checkStatus(String accountNumber);

    Account getById(String accountNumber);
}
