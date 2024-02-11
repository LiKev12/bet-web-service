package com.bet.betwebservice.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.TimeZone;
@Data
@Entity
public class NumberOfPointsInTasksCompletedOverTimeVisualizationDTO {
    @Id
    private UUID id;
    private int numberOfPoints;
    private Integer timestamp;

    public NumberOfPointsInTasksCompletedOverTimeVisualizationDTO(int numberOfPoints, Integer timestamp) {
        this.id = null;
        this.numberOfPoints = numberOfPoints;
        this.timestamp = timestamp;
    }
}
