package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class StampCardsPaginatedModel {
    private int totalN;
    private List<StampCardModel> data;
}
