package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="task_user_task_comment_reaction")
@Data
public class TaskUserTaskCommentReactionEntity {
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

    @Column(name="reaction_type")
    private String reactionType;
}
