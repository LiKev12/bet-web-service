package com.bet.betwebservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="task_user_task_comment_reply")
@Data
public class TaskUserTaskCommentReplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private Integer timestampUnix;

    @Column(name="id__task_comment")
    private UUID idTaskComment;

    @Column(name="id__user")
    private UUID idUser;

    @Column(name="comment_reply_text")
    private String commentReplyText;

    @JsonProperty(value="isText")
    @Column(name="is_text")
    private boolean isText;

    @Column(name="id__comment_reply_image_key")
    private UUID idCommentReplyImageKey;

    @JsonProperty(value="isImage")
    @Column(name="is_image")
    private boolean isImage;
}
