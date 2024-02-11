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
public class UserPageModel {
    private UUID id;
    private String username;
    private String name;
    private String bio;
    private String imageLink;
    private List<UserBubbleModel> userBubblesFollowing;
    private int userBubblesFollowingTotalNumber;
    private List<UserBubbleModel> userBubblesFollower;
    private int userBubblesFollowerTotalNumber;
    @JsonProperty(value="isMe")
    private boolean isMe;
    @JsonProperty(value="isFollowedByMe")
    private boolean isFollowedByMe;
    @JsonProperty(value="isFollowRequestSentNotYetAccepted")
    private boolean isFollowRequestSentNotYetAccepted;
    private int numberOfPendingFollowUserRequests;
}
