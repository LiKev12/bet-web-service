package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@Builder
public class NotificationModel {
    private UUID id;
    private UUID idUser;
    private String notificationType;
    private String notificationMessage;
    private String linkPageType;
    private UUID idLinkPage;
    @JsonProperty(value="isSeen")
    private boolean isSeen;
    private Integer timestampToSortBy;
    private String datetimeDateAndTimeLabel;
    private String imageLink;
    @JsonProperty(value="isDismissed")
    private boolean isDismissed;
    @JsonProperty(value="isMemberOfPod")
    private boolean isMemberOfPod;
    @JsonProperty(value="isFollowedByUserWhoSentFollowRequest")
    private boolean isFollowedByUserWhoSentFollowRequest;
}
