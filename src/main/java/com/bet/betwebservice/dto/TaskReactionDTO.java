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
public class TaskReactionDTO {
    @Id
    private UUID id;
    private UUID idUser;
    private Integer timestampToSortBy;
    private String reactionType;
}
