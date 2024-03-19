package com.example.ledger.application.command;

import com.example.ledger.domain.asset.AssetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BatchMovementCommandItem {
    @NotNull
    private String assetId;
    @NotNull
    private AssetType assetType;
    @Min(1)
    @NotNull
    private BigDecimal amount;
    @NotEmpty
    private String sourceAccount;
    @NotEmpty
    private String targetAccount;
}
