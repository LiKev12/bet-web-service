package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class PersonalPageModel {
    private UUID id;
    private String name;
    private String username;
    private String imageLink;
    private int numberOfPointsTaskCompleteToday;
}
