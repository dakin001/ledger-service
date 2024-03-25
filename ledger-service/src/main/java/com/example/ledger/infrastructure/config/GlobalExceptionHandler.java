package com.example.ledger.infrastructure.config;

import com.example.ledger.domain.shared.model.BusinessException;
import com.example.ledger.domain.shared.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> businessExceptionHandler(BusinessException ex) {
        //  msg for debug in developing
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getClass().getName() + ": " + ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> businessExceptionHandler(BindException ex) {
        //  msg for debug in developing
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }
}
