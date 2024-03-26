package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@Builder
public class PodPageModel {
    private UUID id;
    private String name;
    private String description;
    private String imageLink;
    @JsonProperty(value="isPublic")
    private boolean isPublic;
    @JsonProperty(value="isPodMember")
    private boolean isPodMember;
    @JsonProperty(value="isPodModerator")
    private boolean isPodModerator;
    @JsonProperty(value="isSentBecomePodModeratorRequest")
    private boolean isSentBecomePodModeratorRequest;
    @JsonProperty(value="isReachedNumberOfTasksLimit")
    private boolean isReachedNumberOfTasksLimit;
    private int numberOfPendingBecomeModeratorRequests;
    private List<UserBubbleModel> userBubblesPodMember;
    private int userBubblesPodMemberTotalNumber;
    private List<UserBubbleModel> userBubblesPodModerator;
    private int userBubblesPodModeratorTotalNumber;
}
