package com.example.ledger.infrastructure.repository.jpa;

import com.example.ledger.infrastructure.repository.EventDbModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventJpaRepository extends JpaRepository<EventDbModel, Long> {

    List<EventDbModel> getByAggregateId(String aggregateId);
}
