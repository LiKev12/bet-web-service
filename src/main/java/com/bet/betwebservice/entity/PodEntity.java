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
    private int timestampUnix;

    @Column(name="id__user_create")
    private String idUserCreate;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @JsonProperty(value="isPublic")
    @Column(name="is_public")
    private boolean isPublic;

    @JsonProperty(value="isRequireApproveRequestToJoin")
    @Column(name="is_require_approve_request_to_join")
    private boolean isRequireApproveRequestToJoin;

    @Column(name="image")
    private String image;
}
