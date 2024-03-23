package com.example.ledger.infrastructure.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "event_record", indexes = {
        @Index(name = "ux_aggregate_id_version", columnList = "aggregateId,version", unique = true)
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
