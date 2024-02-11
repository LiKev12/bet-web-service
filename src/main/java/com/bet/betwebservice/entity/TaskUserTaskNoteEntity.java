package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="task_user_task_note")
@Data
public class TaskUserTaskNoteEntity {
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

    @Column(name="note_text")
    private String noteText;

    @Column(name="timestamp_note_text")
    private Integer timestampNoteText;

    @Column(name="id__note_image_key")
    private UUID idNoteImageKey;

    @Column(name="timestamp_note_image")
    private Integer timestampNoteImage;
}
