package com.example.ledger.domain.movement;

import com.example.ledger.domain.asset.Asset;
import com.example.ledger.domain.asset.AssetType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Movement {
    private String serialNumber;
    private String assetId;
    private AssetType assetType;
    private BigDecimal amount;
    private String sourceAccount;
    private String targetAccount;

    public Asset getAsset() {
        var asset = new Asset(this.getAssetType());
        asset.setId(this.getAssetId());
        asset.setAmount(this.getAmount());
        return asset;
    }
}
