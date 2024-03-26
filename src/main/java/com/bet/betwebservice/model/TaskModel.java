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
public class TaskModel {
    private UUID id;
    private String name;
    private String description;
    private String imageLink;
    private int numberOfPoints;
    private UUID idPod;
    @JsonProperty(value="isComplete")
    private boolean isComplete;
    @JsonProperty(value="isStar")
    private boolean isStar;
    @JsonProperty(value="isPin")
    private boolean isPin;
    private String noteText;
    private String noteImageLink;
    private String datetimeCreate;
    private String datetimeUpdate;
    private String datetimeTarget;
    private String datetimeComplete;
    private List<UserBubbleModel> userBubblesTaskComplete;
    private int userBubblesTaskCompleteTotalNumber;
    @JsonProperty(value="isMemberOfTaskPod")
    private boolean isMemberOfTaskPod;
    @JsonProperty(value="isTaskPodPrivate")
    private boolean isTaskPodPrivate;
}
