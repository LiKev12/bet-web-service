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
public class TaskIndividualPropertiesDTO {
    @Id
    private UUID id;
    private String noteText;
    private UUID idNoteImageKey;
    @JsonProperty(value="isComplete")
    private boolean isComplete;
    @JsonProperty(value="isStar")
    private boolean isStar;
    @JsonProperty(value="isPin")
    private boolean isPin;
    private Integer timestampComplete;
}
