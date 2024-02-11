package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="task_user_task_pin")
@Data
public class TaskUserTaskPinEntity {
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
}
