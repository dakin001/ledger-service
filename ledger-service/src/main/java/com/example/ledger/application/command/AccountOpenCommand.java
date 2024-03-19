package com.example.ledger.application.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountOpenCommand {
    private String serialNumber;
    private String userId;
}
