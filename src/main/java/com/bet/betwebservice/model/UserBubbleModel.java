package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserBubbleModel {
    private UUID id;
    private String name;
    private String username;
    private String imageLink;
    private Integer timestampToSortBy;
    private String datetimeDateOnlyLabel;
    private String datetimeDateAndTimeLabel;
    @JsonProperty(value="isFollowedByMe")
    private boolean isFollowedByMe;
    @JsonProperty(value="isFollowRequestSentNotYetAccepted")
    private boolean isFollowRequestSentNotYetAccepted;
    @JsonProperty(value="isMe")
    private boolean isMe;
}
