package com.example.ledger.domain.movement;

import com.example.ledger.application.command.DepositCommand;
import com.example.ledger.application.command.FrozeCommand;
import com.example.ledger.application.command.WithdrawCommand;
import com.example.ledger.application.command.service.AccountService;
import com.example.ledger.domain.movement.event.*;
import com.example.ledger.domain.shared.model.AggregateRoot;
import com.example.ledger.domain.shared.model.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class BatchMovement extends AggregateRoot {
    private MovementStatus status = MovementStatus.PENDING;
    @Getter
    private List<Movement> movements = new ArrayList<>();
    private Queue<Movement> frozeQueue = new LinkedList<>();
    private Queue<Movement> unfrozeQueue = new LinkedList<>();
    private Queue<Movement> withdrawQueue = new LinkedList<>();
    private Queue<Movement> depositQueue = new LinkedList<>();

    private AccountService accountService;
    private static final String MOVEMENT_ID_PREFIX = "Movement-";

    public BatchMovement(String id) {
        super(id);
    }

    public BatchMovement(List<Movement> movements) {
        super(genNewId());

        fillSerialNumber(movements);

        BatchMovementCreateEvent event = new BatchMovementCreateEvent();
        event.setMovements(movements);
        super.apply(event);
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public boolean isFinalStatus() {
        return status == MovementStatus.CLEARED
                || status == MovementStatus.FAILED;
    }

    public void execute() {
        if (isFinalStatus()) {
            return;
        }

        try {
            this.prepare();
        } catch (Exception ex) {
            log.error("prepare failed ", ex);
            try {
                this.cancel();
            } catch (Exception e) {
                log.error("cancel failed ", ex);
            }
            return;
        }
        // always delivery cancel or commit.
        try {
            this.commit();
        } catch (Exception e) {
            log.error("commit failed ", e);
        }
    }

    private void fillSerialNumber(List<Movement> movements) {
        for (int i = 0; i < movements.size(); i++) {
            movements.get(i).setSerialNumber(super.id + i);
        }
    }

    private void prepare() {
        if (status == MovementStatus.PROCESSING) {
            return;
        }

        if (status == MovementStatus.PROCESSING_ROLLBACK) {
            // jump to cancel
            throw new BusinessException("rollback");
        }
        // Pending
        validate();
        froze();
    }

    private void commit() {
        this.withdraw();
        this.deposit();
    }

    private void cancel() {
        this.unfroze();
    }

    private void validate() {
        for (var item : movements) {
            if (StringUtils.equals(item.getSourceAccount(), item.getTargetAccount())) {
                throw new BusinessException("source account and target account are same");
            }

            accountService.checkStatus(item.getSourceAccount());
            accountService.checkStatus(item.getTargetAccount());
        }
    }

    private void froze() {
        List<Movement> successList = new ArrayList<>();

        try {
            for (var item : frozeQueue) {
                froze(item);
                successList.add(item);
            }
            super.apply(new MovementFrozeSuccessEvent());
        } catch (Exception ex) {
            var event = new MovementFrozeFailEvent();
            event.setItems(successList);
            super.apply(event);

            log.error("froze fail", ex);
            throw ex;
        }
    }

    private void froze(Movement item) {
        FrozeCommand command = new FrozeCommand();
        command.setSerialNumber(item.getSerialNumber());
        command.setAsset(item.getAsset());
        command.setAccountId(item.getSourceAccount());
        accountService.froze(command);
    }

    private void unfroze() {
        unfrozeQueue.forEach(this::unfroze);

        super.apply(new MovementFailEvent());
    }

    private void unfroze(Movement item) {
        FrozeCommand command = new FrozeCommand();
        command.setSerialNumber(item.getSerialNumber());
        command.setAsset(item.getAsset());
        command.setAccountId(item.getSourceAccount());
        accountService.unfroze(command);
    }

    private void withdraw() {
        withdrawQueue.forEach(this::withdraw);

        super.apply(new MovementWithdrawSuccessEvent());
    }

    private void withdraw(Movement item) {
        var command = new WithdrawCommand();
        command.setSerialNumber(item.getSerialNumber());
        command.setAsset(item.getAsset());
        command.setAccountId(item.getSourceAccount());
        accountService.withdraw(command);
    }

    private void deposit() {
        depositQueue.forEach(this::deposit);

        super.apply(new MovementDepositSuccessEvent());
    }

    private void deposit(Movement item) {
        var command = new DepositCommand();
        command.setSerialNumber(item.getSerialNumber());
        command.setAsset(item.getAsset());
        command.setAccountId(item.getTargetAccount());
        accountService.deposit(command);
    }

    private static String genNewId() {
        return MOVEMENT_ID_PREFIX + UUID.randomUUID().toString();
    }

    public void apply(BatchMovementCreateEvent event) {
        movements = event.getMovements();

        frozeQueue.addAll(movements);
    }

    public void apply(MovementFrozeSuccessEvent event) {
        withdrawQueue.addAll(movements);
        status = MovementStatus.PROCESSING;
    }

    public void apply(MovementFrozeFailEvent event) {
        unfrozeQueue.addAll(event.getItems());
        status = MovementStatus.PROCESSING_ROLLBACK;
    }

    public void apply(MovementFailEvent event) {
        status = MovementStatus.FAILED;
    }

    public void apply(MovementWithdrawSuccessEvent event) {
        depositQueue.addAll(movements);
    }

    public void apply(MovementDepositSuccessEvent event) {
        status = MovementStatus.CLEARED;
    }
}
