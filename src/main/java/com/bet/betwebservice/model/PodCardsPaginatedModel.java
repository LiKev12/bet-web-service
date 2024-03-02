package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PodCardsPaginatedModel {
    private int totalN;
    private List<PodCardModel> data;
}
