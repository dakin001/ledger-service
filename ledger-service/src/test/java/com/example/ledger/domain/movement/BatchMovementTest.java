package com.example.ledger.domain.movement;

import com.example.ledger.application.command.service.AccountService;
import com.example.ledger.application.command.service.impl.AccountServiceImpl;
import com.example.ledger.domain.account.exception.AccountClosedException;
import com.example.ledger.domain.account.exception.NoEnoughAmountException;
import com.example.ledger.domain.asset.Asset;
import com.example.ledger.domain.asset.AssetType;
import com.example.ledger.domain.movement.event.MovementFailEvent;
import com.example.ledger.domain.movement.event.MovementFrozeFailEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BatchMovementTest {
    AccountService accountService = mock(AccountServiceImpl.class);
    BatchMovement batchMovement;

    @BeforeEach
    void setUp() {
        batchMovement = new BatchMovement(createMovements());
        batchMovement.setAccountService(accountService);
    }

    @Test
    void execute_sameAccount_returnFailEvent() {
        // CASE
        var movement = batchMovement.getMovements().get(0);
        movement.setTargetAccount(movement.getSourceAccount());

        // WHEN
        batchMovement.execute();

        // THEN
        assertEquals(MovementFailEvent.class, batchMovement.getChanges().get(1).getClass());
        verify(accountService, Mockito.never()).checkStatus(any());
    }

    @Test
    void execute_wrongAccount_returnFailEvent() {
        // CASE
        doThrow(new AccountClosedException()).when(accountService).checkStatus(anyString());

        // WHEN
        batchMovement.execute();

        // THEN
        assertEquals(MovementFailEvent.class, batchMovement.getChanges().get(1).getClass());
        verify(accountService, Mockito.atLeastOnce()).checkStatus(any());
    }

    @Test
    void execute_frozeSecondItemFail_unfrozeFirstAndReturnFrozeFailEvent() {
        // CASE
        doNothing().doThrow(new NoEnoughAmountException()).when(accountService).froze(any());

        // WHEN
        batchMovement.execute();

        // THEN
        assertEquals(MovementFrozeFailEvent.class, batchMovement.getChanges().get(batchMovement.getChanges().size() - 2).getClass());
        assertEquals(MovementFailEvent.class, batchMovement.getChanges().get(batchMovement.getChanges().size() - 1).getClass());
        verify(accountService, Mockito.times(2)).froze(any());
        verify(accountService, Mockito.times(1)).unfroze(any());
    }

    @Test
    void execute_withdrawAndDeposit_success() {
        // CASE

        // WHEN
        batchMovement.execute();

        // THEN
        verify(accountService, Mockito.times(2)).withdraw(any());
        verify(accountService, Mockito.times(2)).deposit(any());
        assertTrue(batchMovement.isFinalStatus());
    }

    @Test
    void execute_withdraw_fail() {
        // CASE
        doThrow(new RuntimeException("test mock exception")).when(accountService).withdraw(any());

        // WHEN
        batchMovement.execute();

        // THEN
        verify(accountService, Mockito.times(1)).withdraw(any());
        verify(accountService, Mockito.never()).deposit(any());
        assertFalse(batchMovement.isFinalStatus());
    }

    @Test
    void execute_deposit_fail() {
        // CASE
        doThrow(new RuntimeException("test mock exception")).when(accountService).deposit(any());

        // WHEN
        batchMovement.execute();

        // THEN
        assertFalse(batchMovement.isFinalStatus());
    }

    List<Movement> createMovements() {
        List<Movement> movements = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Asset asset = createTestAsset(BigDecimal.valueOf(10));
            var item = new Movement();
            item.setSourceAccount("A1");
            item.setTargetAccount("A2");
            item.setAssetId(asset.getId());
            item.setAmount(asset.getAmount());
            item.setAssetType(asset.getAssetType());
            movements.add(item);
        }

        return movements;
    }

    Asset createTestAsset(BigDecimal amount) {
        var asset = new Asset(AssetType.CURRENCY);
        asset.setId(UUID.randomUUID().toString());
        asset.setAmount(amount);
        return asset;
    }
}