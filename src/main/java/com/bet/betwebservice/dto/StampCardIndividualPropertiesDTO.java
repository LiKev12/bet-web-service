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
public class StampCardIndividualPropertiesDTO {

    @Id
    private UUID id;
    @JsonProperty(value="isCollect")
    private boolean isCollect;
}
