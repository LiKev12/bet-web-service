package com.bet.betwebservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="pod")
@Data
public class PodEntity {
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

    @JsonProperty(value="isPublic")
    @Column(name="is_public")
    private boolean isPublic;
    
    @Column(name="id__image_key")
    private UUID idImageKey;
}
