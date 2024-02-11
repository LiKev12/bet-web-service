package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@Builder
public class PodCardModel {
    private UUID id;
    private String name;
    private String description;
    private String imageLink;
    @JsonProperty(value="isPublic")
    private boolean isPublic;
    private int numberOfMembers;
    @JsonProperty(value="isMember")
    private boolean isMember;
    @JsonProperty(value="isModerator")
    private boolean isModerator;
}
