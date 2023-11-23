package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="task")
@Data
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private int timestampUnix;

    @Column(name="id__user_create")
    private UUID idUserCreate;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="image")
    private String image;

    @Column(name="number_of_points")
    private int numberOfPoints;

    @Column(name="timestamp_update")
    private int timestampUpdate;

    @Column(name="timestamp_target")
    private int timestampTarget;

    @Column(name="id__pod")
    private UUID idPod;
}
