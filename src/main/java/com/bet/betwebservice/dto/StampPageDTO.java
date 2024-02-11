package com.bet.betwebservice.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Entity
public class StampPageDTO {
    @Id
    private UUID id;
    private String name;
    private String description;
    private UUID idImageKey;
}
