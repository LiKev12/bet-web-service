package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class StampCardModel {
    private UUID id;
    private String name;
    private String description;
    private String image;
    private int numberOfUsersCollect;
}
