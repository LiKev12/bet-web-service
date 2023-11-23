package com.bet.betwebservice.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Entity
public class TaskDTO {
    @Id
    private UUID id;
    private String name;
    private String description;
    private String image;
    private int numberOfPoints;
    private UUID idPod;
    private boolean isComplete;
    private boolean isStar;
    private boolean isPin;
    private Integer timestampUnix;
    private Integer timestampUpdate;
    private Integer timestampTarget;
    private Integer timestampComplete;

}
