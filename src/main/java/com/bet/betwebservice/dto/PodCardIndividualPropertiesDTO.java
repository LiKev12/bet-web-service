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
public class PodCardIndividualPropertiesDTO {

    @Id
    private UUID id;

    @JsonProperty(value="isMember")
    private boolean isMember;
    @JsonProperty(value="isModerator")
    private boolean isModerator;
}
