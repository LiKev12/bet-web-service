package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class StampPageModel {
    private UUID id;
    @JsonProperty(value="isCreatedByMe")
    private boolean isCreatedByMe;
    private String idUserCreate;
    private String usernameUserCreate;
    private String name;
    private String description;
    private String imageLink;
    private List<UserBubbleModel> userBubblesStampCollect;
    private int userBubblesStampCollectTotalNumber;
    @JsonProperty(value="isCollectedByMe")
    private boolean isCollectedByMe;
    @JsonProperty(value="isEligibleToBeCollectedByMe")
    private boolean isEligibleToBeCollectedByMe;
}

