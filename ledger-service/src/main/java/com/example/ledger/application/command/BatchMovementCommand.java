package com.example.ledger.application.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BatchMovementCommand {
    @NotEmpty
    private String serialNumber;
    @Valid
    private List<BatchMovementCommandItem> details = new ArrayList<>();
}
