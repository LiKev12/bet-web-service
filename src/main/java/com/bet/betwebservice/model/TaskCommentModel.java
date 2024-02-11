package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TaskCommentModel {
    private String idTaskComment;
    private String idUser;
    private String username;
    private String userImageLink;
    private Integer timestampToSortBy;
    private String datetimeDateAndTimeLabel;
    @JsonProperty(value="isText")
    private boolean isText;
    private String commentText;
    @JsonProperty(value="isImage")
    private boolean isImage;
    private String commentImageLink;
    private int numberOfReplies;
    private ReactionsModel reactions;
}
