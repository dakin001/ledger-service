package com.example.ledger.application.command;

import com.example.ledger.domain.asset.Asset;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseAssetCommand {
    private String serialNumber;
    private String userId;
    private String accountId;
    private Asset asset;
}
