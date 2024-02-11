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
public class TaskCommentsAndReactionsModel {
    private List<UserBubbleReactionModel> userBubblesReactionTask;
    private int userBubblesReactionTaskTotalNumber;
    private List<TaskCommentModel> taskComments;
    private String myReactionType;
    
    @Data
    @AllArgsConstructor
    @Builder
    public static class TaskCommentModel {
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
        private List<UserBubbleReactionModel> userBubblesReactionTaskComment;
        private int userBubblesReactionTaskCommentTotalNumber;
        private List<TaskCommentReplyModel> taskCommentReplies;
        private String myReactionType;

    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class TaskCommentReplyModel {
        private String idUser;
        private String username;
        private String userImageLink;
        private Integer timestampToSortBy;
        private String datetimeDateAndTimeLabel;
        @JsonProperty(value="isText")
        private boolean isText;
        private String commentReplyText;
        @JsonProperty(value="isImage")
        private boolean isImage;
        private String commentReplyImageLink;
        private List<UserBubbleReactionModel> userBubblesReactionTaskCommentReply;
        private int userBubblesReactionTaskCommentReplyTotalNumber;
        private String myReactionType;
    }
}
