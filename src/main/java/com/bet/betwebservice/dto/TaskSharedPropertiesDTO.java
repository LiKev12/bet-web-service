package com.bet.betwebservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Entity
public class TaskSharedPropertiesDTO {
    @Id
    private UUID id;
    private String name;
    private String description;
    private UUID idImageKey;
    private int numberOfPoints;
    private UUID idPod;
    private Integer timestampUnix;
    private Integer timestampUpdate;
    private String datetimeTarget;
}
