package com.example.ledger.domain.asset;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AssetSnapshot {
    private String id;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal frozenAmount = BigDecimal.ZERO;
    private AssetType assetType;
}
