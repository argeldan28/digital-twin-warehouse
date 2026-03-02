package com.warehouse.digitaltwin.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.digitaltwin.domain.model.Warehouse;
import com.warehouse.digitaltwin.infrastructure.persistence.entities.EventLogEntity;
import com.warehouse.digitaltwin.infrastructure.persistence.repositories.EventLogRepository;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service


public class EventLoggerService {

    private static final Logger log = LoggerFactory.getLogger(EventLoggerService.class);

    private final EventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    @Async
    public void logState(Warehouse warehouse) {
        try {
            String payload = objectMapper.writeValueAsString(warehouse);

            EventLogEntity event = EventLogEntity.builder()
                    .warehouseId(warehouse.getId())
                    .eventType("STATE_UPDATE")
                    .payload(payload)
                    .timestamp(LocalDateTime.now())
                    .build();

            eventLogRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize warehouse state for event logging", e);
        }
    }
}
