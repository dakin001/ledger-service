package com.example.ledger.interfaces;

import com.example.ledger.application.command.AccountCloseCommand;
import com.example.ledger.application.command.AccountOpenCommand;
import com.example.ledger.application.command.service.AccountService;
import com.example.ledger.application.query.BalanceQueryDto;
import com.example.ledger.application.query.PageResult;
import com.example.ledger.application.query.QueryService;
import com.example.ledger.application.query.WalletProjection;
import com.example.ledger.domain.shared.util.LoginContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final QueryService queryService;

    @Operation(summary = "open an account", description = "open an account", tags = {"Account"})
    @PostMapping()
    public ResponseEntity<Object> open() {
        AccountOpenCommand openCommand = new AccountOpenCommand();
        openCommand.setUserId(LoginContextUtils.getCurrentUser());
        var account = accountService.open(openCommand);

        return ResponseEntity.created(URI.create("/accounts/" + account.getAccountNumber())).build();
    }

    @Operation(summary = "close an account", description = "close an account", tags = {"Account"})
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> close(@PathVariable("accountId") String accountId) {
        var command = new AccountCloseCommand();
        command.setUserId(LoginContextUtils.getCurrentUser());
        command.setAccountId(accountId);

        accountService.close(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "request any historical balance of a wallet.",
            description = "request any historical balance of a wallet.", tags = {"Account"})
    @GetMapping("/{accountId}/wallets/{walletId}/balance")
    public PageResult<WalletProjection> getBalance(@PathVariable("accountId") @Schema(example = "ABCDE001") String accountId,
                                                   @PathVariable("walletId") @Schema(example = "CURRENCY") String walletId,
                                                   @Valid BalanceQueryDto query) {
        query.setAccountNumber(accountId);
        query.setWalletType(walletId);

        return queryService.getBalance(query);
    }
}
