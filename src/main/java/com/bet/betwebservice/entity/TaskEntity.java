package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="task")
@Data
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private Integer timestampUnix;

    @Column(name="id__user_create")
    private UUID idUserCreate;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="id__image_key")
    private UUID idImageKey;

    @Column(name="number_of_points")
    private Integer numberOfPoints;

    @Column(name="timestamp_update")
    private Integer timestampUpdate;

    @Column(name="datetime_target")
    private String datetimeTarget;

    @Column(name="id__pod")
    private UUID idPod;

    @JsonProperty(value="isArchived")
    @Column(name="is_archived")
    private boolean isArchived;
}
