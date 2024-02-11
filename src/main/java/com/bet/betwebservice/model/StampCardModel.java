package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@Builder
public class StampCardModel {
    private UUID id;
    private String name;
    private String description;
    private String imageLink;
    private int numberOfUsersCollect;
    
    @JsonProperty(value="isPublic")
    private boolean isPublic;
    @JsonProperty(value="isCollect")
    private boolean isCollect;

}
