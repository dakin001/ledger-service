package com.example.ledger.domain.account.exception;

import com.example.ledger.domain.shared.model.BusinessException;

public class NoPermissionException extends BusinessException {
    public NoPermissionException() {
    }

    public NoPermissionException(String message) {
        super(message);
    }
}
