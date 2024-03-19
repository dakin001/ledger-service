package com.example.ledger.application.command.mapper;

import com.example.ledger.application.command.BatchMovementCommandItem;
import com.example.ledger.domain.movement.Movement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MovementCommandMapper {
    MovementCommandMapper INSTANCE = Mappers.getMapper(MovementCommandMapper.class);

    @Mapping(target = "serialNumber", ignore = true)
    Movement toMovement(BatchMovementCommandItem commandItem);

    List<Movement> toMovement(List<BatchMovementCommandItem> commandItemList);
}
