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
public class StampCardSharedPropertiesDTO {

    @Id
    private UUID id;
    private String name;
    private String description;
    private UUID idImageKey;
    private int numberOfUsersCollect;
    @JsonProperty(value="isPublic")
    private boolean isPublic;
}
