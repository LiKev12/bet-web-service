package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class TaskModel {
    private UUID id;
    private String name;
    private String description;
    private String image;
    private int numberOfPoints;
    private UUID idPod;
    private String datetimeCreate;
    private String datetimeUpdate;
    private String datetimeTarget;
    private String datetimeComplete;

    @JsonProperty(value="isComplete")
    private boolean isComplete;
    @JsonProperty(value="isStar")
    private boolean isStar;
    @JsonProperty(value="isPin")
    private boolean isPin;
}
