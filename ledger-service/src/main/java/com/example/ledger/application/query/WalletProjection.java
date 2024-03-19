package com.example.ledger.application.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "wallet", indexes = {
        @Index(name = "idx_account_number", columnList = "accountNumber")
})
@Entity
public class WalletProjection {
    @Id
    private Long id;
    private String userId;
    private String accountNumber;
    private String walletType;
    private BigDecimal balance;
    private Integer version;
    private LocalDateTime timestamp;
}
