package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="stamp")
@Data
public class StampEntity {
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
}
