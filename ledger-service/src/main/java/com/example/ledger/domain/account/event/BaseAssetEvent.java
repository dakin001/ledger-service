package com.example.ledger.domain.account.event;

import com.example.ledger.domain.asset.Asset;
import com.example.ledger.domain.shared.model.AggregateRoot;
import com.example.ledger.domain.shared.model.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseAssetEvent<T extends AggregateRoot> extends Event<T> {
    private Asset asset;
}
