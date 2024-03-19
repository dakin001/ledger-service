package com.example.ledger.application.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCloseCommand {
    private String serialNumber;
    private String userId;
    private String accountId;
}
