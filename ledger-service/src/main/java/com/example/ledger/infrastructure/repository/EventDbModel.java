package com.example.ledger.infrastructure.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "event_record", indexes = {
        @Index(name = "idx_aggregate_id", columnList = "aggregateId")
})
public class EventDbModel {
    @Id
    private Long id;
    private String aggregateId;
    private Integer version;
    private String name;
    @Lob
    private byte[] data;
}
