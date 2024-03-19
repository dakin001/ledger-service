package com.example.ledger.interfaces;

import com.example.ledger.application.command.BatchMovementCommand;
import com.example.ledger.application.command.service.MovementService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class LedgerController {
    private final MovementService movementService;

    @Operation(summary = "batch move", description = "", tags = {"Movement"})
    @PostMapping("movements")
    public void createMovement(@Validated @RequestBody BatchMovementCommand command) {
        movementService.createRequest(command);
    }

    @Operation(summary = "Ledger Balance", description = "return user balance by sse", tags = {"Ledger"})
    @GetMapping(value = "Balance", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter getBalance() throws IOException {
        // todo
        var sseEmitter = new SseEmitter(Duration.ofMinutes(60L).toMillis());
        sseEmitter.send(BigDecimal.valueOf(10.00));

        return sseEmitter;
    }
}
