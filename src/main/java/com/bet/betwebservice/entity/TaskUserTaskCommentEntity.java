package com.bet.betwebservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="task_user_task_comment")
@Data
public class TaskUserTaskCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private Integer timestampUnix;

    @Column(name="id__task")
    private UUID idTask;

    @Column(name="id__user")
    private UUID idUser;

    @JsonProperty(value="isText")
    @Column(name="is_text")
    private boolean isText;

    @Column(name="comment_text")
    private String commentText;

    @JsonProperty(value="isImage")
    @Column(name="is_image")
    private boolean isImage;

    @Column(name="id__comment_image_key")
    private UUID idCommentImageKey;
}
