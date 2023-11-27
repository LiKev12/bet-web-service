package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class PodPageModel {
    private UUID id;
    private String name;
    private String description;
    private String image;
    // TODO, this is for pod page
}
